package io.github.tonnyl.moka

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.work.*
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.util.CoilUtils
import io.github.tonnyl.moka.work.NotificationWorker
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.data.SignedInAccounts
import io.tonnyl.moka.common.network.KtorClient
import io.tonnyl.moka.common.store.AccountSerializer
import io.tonnyl.moka.common.store.SettingSerializer
import io.tonnyl.moka.common.store.data.NotificationSyncInterval
import io.tonnyl.moka.common.store.data.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@ExperimentalPagingApi
@ExperimentalSerializationApi
class MokaApp : Application(), ImageLoaderFactory {

    val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .cache(CoilUtils.createDefaultCache(this))
            .build()
    }

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val settingsDataStore: DataStore<Settings> by dataStore(
        fileName = "global_settings.pb",
        serializer = SettingSerializer
    )

    val accountsDataStore: DataStore<SignedInAccounts> by dataStore(
        fileName = "accounts.pb",
        serializer = AccountSerializer
    )

    val accountInstancesLiveData by lazy {
        accountsDataStore.data.map { accounts ->
            accounts.accounts.map { account ->
                AccountInstance(
                    application = this,
                    unauthenticatedKtorClient = KtorClient.unauthenticatedKtorClient,
                    signedInAccount = account
                )
            }
        }.asLiveData(timeoutInMs = Long.MAX_VALUE)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .componentRegistry {
                add(SvgDecoder(applicationContext))
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder(applicationContext))
                } else {
                    add(GifDecoder())
                }
            }
            .crossfade(enable = true)
            .okHttpClient {
                okHttpClient
            }
            .build()
    }

    fun triggerNotificationWorker() {
        applicationScope.launch {
            try {
                settingsDataStore.data.collect { settings ->
                    if (settings.enableNotifications) {
                        val intervalTimePeriod = when (settings.notificationSyncInterval) {
                            NotificationSyncInterval.ONE_QUARTER -> {
                                15L
                            }
                            NotificationSyncInterval.THIRTY_MINUTES -> {
                                30L
                            }
                            NotificationSyncInterval.ONE_HOUR -> {
                                60L
                            }
                            NotificationSyncInterval.TWO_HOURS -> {
                                60 * 2L
                            }
                            NotificationSyncInterval.SIX_HOURS -> {
                                60 * 6L
                            }
                            NotificationSyncInterval.TWELVE_HOURS -> {
                                60 * 12L
                            }
                            NotificationSyncInterval.TWENTY_FOUR_HOURS -> {
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
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }
            }
        }
    }

}