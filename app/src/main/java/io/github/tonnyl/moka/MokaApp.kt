package io.github.tonnyl.moka

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingConfig
import androidx.preference.PreferenceManager
import androidx.work.*
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.CoilUtils
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.util.mapToAccountTokenUserTriple
import io.github.tonnyl.moka.work.NotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MokaApp : Application(), ImageLoaderFactory {

    private val accountManager by lazy(LazyThreadSafetyMode.NONE) {
        AccountManager.get(this)
    }

    val loginAccounts = MutableLiveData<List<Triple<Account, String, AuthenticatedUser>>>()
    val theme by lazy(LazyThreadSafetyMode.NONE) {
        MutableLiveData<String>(
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
                .getString("key_choose_theme", "0")
        )
    }

    companion object {
        const val PER_PAGE = 16
        const val MAX_SIZE_OF_PAGED_LIST = 1024

        val defaultPagingConfig = PagingConfig(
            pageSize = PER_PAGE,
            prefetchDistance = PER_PAGE,
            enablePlaceholders = false,
            initialLoadSize = PER_PAGE,
            maxSize = MAX_SIZE_OF_PAGED_LIST
        )
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        accountManager.addOnAccountsUpdatedListener(
            { accounts ->
                GlobalScope.launch(Dispatchers.IO) {
                    loginAccounts.postValue(
                        accounts.sortedByDescending {
                            accountManager.getPassword(it).toLong()
                        }.map {
                            it.mapToAccountTokenUserTriple(accountManager)
                        }.filterNotNull()
                            .toMutableList()
                    )
                }

                if (accounts.isNotEmpty()) {
                    triggerNotificationWorker(true)
                }
            },
            null,
            true
        )
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .crossfade(enable = true)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .cache(CoilUtils.createDefaultCache(applicationContext))
                    .build()
            }
            .build()
    }

    fun triggerNotificationWorker(start: Boolean) {
        if (start) {
            if (!PreferenceManager.getDefaultSharedPreferences(applicationContext)
                    .getBoolean("key_enable_notifications", true)
            ) {
                return
            }

            val interval = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                .getString("key_sync_interval", "15")
                ?.toLong() ?: 15L

            WorkManager.getInstance(applicationContext)
                .enqueueUniquePeriodicWork(
                    NotificationWorker::class.java.simpleName,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    PeriodicWorkRequestBuilder<NotificationWorker>(
                        interval,
                        TimeUnit.MINUTES
                    ).setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .setRequiresBatteryNotLow(true)
                            .build()
                    ).addTag(NotificationWorker.WORKER_TAG)
                        .build()
                )
        } else {
            WorkManager.getInstance(applicationContext)
                .cancelAllWorkByTag(NotificationWorker.WORKER_TAG)
        }
    }

}