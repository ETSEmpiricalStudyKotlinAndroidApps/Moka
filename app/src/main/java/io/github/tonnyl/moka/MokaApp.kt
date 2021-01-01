package io.github.tonnyl.moka

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.createDataStore
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingConfig
import androidx.work.*
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.CoilUtils
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.proto.RecentEmojis
import io.github.tonnyl.moka.proto.Settings
import io.github.tonnyl.moka.proto.SignedInAccounts
import io.github.tonnyl.moka.serializers.store.AccountSerializer
import io.github.tonnyl.moka.serializers.store.EmojiSerializer
import io.github.tonnyl.moka.serializers.store.SettingSerializer
import io.github.tonnyl.moka.util.mapToAccountTokenUserTriple
import io.github.tonnyl.moka.work.NotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
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

    val settingsDataStore: DataStore<Settings> by lazy {
        createDataStore(
            fileName = "global_settings.pb",
            serializer = SettingSerializer
        )
    }

    val accountsDataStore: DataStore<SignedInAccounts> by lazy {
        createDataStore(
            fileName = "accounts.pb",
            serializer = AccountSerializer
        )
    }

    // todo make it user-related
    val recentEmojis: DataStore<RecentEmojis> by lazy {
        createDataStore(
            fileName = "recent_emojis.pb",
            serializer = EmojiSerializer
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
                    triggerNotificationWorker()
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

    fun triggerNotificationWorker() {
        GlobalScope.launch {
            settingsDataStore.data.collect { settings ->
                if (settings.enableNotifications) {
                    val intervalTimePeriod = when (settings.notificationSyncInterval) {
                        Settings.NotificationSyncInterval.ONE_QUARTER,
                        Settings.NotificationSyncInterval.UNRECOGNIZED,
                        null -> {
                            15L
                        }
                        Settings.NotificationSyncInterval.THIRTY_MINUTES -> {
                            30L
                        }
                        Settings.NotificationSyncInterval.ONE_HOUR -> {
                            60L
                        }
                        Settings.NotificationSyncInterval.TWO_HOURS -> {
                            60 * 2L
                        }
                        Settings.NotificationSyncInterval.SIX_HOURS -> {
                            60 * 6L
                        }
                        Settings.NotificationSyncInterval.TWELVE_HOURS -> {
                            60 * 12L
                        }
                        Settings.NotificationSyncInterval.TWENTY_FOUR_HOURS -> {
                            60 * 24L
                        }
                    }
                    WorkManager.getInstance(applicationContext)
                        .enqueueUniquePeriodicWork(
                            NotificationWorker::class.java.simpleName,
                            ExistingPeriodicWorkPolicy.REPLACE,
                            PeriodicWorkRequestBuilder<NotificationWorker>(
                                intervalTimePeriod,
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
    }

}