package io.github.tonnyl.moka

import android.accounts.AccountManager
import android.accounts.OnAccountsUpdateListener
import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.asLiveData
import androidx.paging.PagingConfig
import androidx.work.*
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.CoilUtils
import io.github.tonnyl.moka.data.AccessToken
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.data.extension.toPBAccessToken
import io.github.tonnyl.moka.data.extension.toPbAccount
import io.github.tonnyl.moka.network.KtorClient
import io.github.tonnyl.moka.serializers.store.AccountSerializer
import io.github.tonnyl.moka.serializers.store.SettingSerializer
import io.github.tonnyl.moka.serializers.store.data.NotificationSyncInterval
import io.github.tonnyl.moka.serializers.store.data.Settings
import io.github.tonnyl.moka.serializers.store.data.SignedInAccount
import io.github.tonnyl.moka.serializers.store.data.SignedInAccounts
import io.github.tonnyl.moka.ui.auth.Authenticator
import io.github.tonnyl.moka.util.json
import io.github.tonnyl.moka.work.NotificationWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.concurrent.TimeUnit

@ExperimentalSerializationApi
class MokaApp : Application(), ImageLoaderFactory {

    val accountManager: AccountManager by lazy {
        AccountManager.get(this)
    }

    val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .cache(CoilUtils.createDefaultCache(this))
            .build()
    }

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val accountListener by lazy {
        OnAccountsUpdateListener { onDeviceAccountsRawData ->
            applicationScope.launch(Dispatchers.IO) {
                try {
                    val onDeviceAccountAndTokenPairs = onDeviceAccountsRawData.mapNotNull {
                        val account = runCatching {
                            json.decodeFromString<AuthenticatedUser>(
                                accountManager.getUserData(
                                    it,
                                    Authenticator.KEY_AUTH_USER_INFO
                                )
                            )
                        }.getOrNull()
                        val token = runCatching {
                            json.decodeFromString<AccessToken>(
                                accountManager.blockingGetAuthToken(
                                    it,
                                    Authenticator.KEY_AUTH_TOKEN,
                                    true
                                )
                            )
                        }.getOrNull()
                        if (account == null || token == null) {
                            null
                        } else {
                            Pair(account, token)
                        }
                    }

                    val sortedAccounts = mutableListOf<Pair<AuthenticatedUser, AccessToken>>()

                    val existingAccounts = accountsDataStore.data.single().accounts
                    existingAccounts.forEach { signedInAccount ->
                        val onDeviceAccount =
                            onDeviceAccountAndTokenPairs.find { it.first.id == signedInAccount.account.id }
                        if (onDeviceAccount != null) {
                            sortedAccounts.add(onDeviceAccount)
                        }
                    }

                    onDeviceAccountAndTokenPairs.forEach { onDeviceAccountAndTokenPair ->
                        val hasAdded =
                            sortedAccounts.find { it.first.id == onDeviceAccountAndTokenPair.first.id }
                        if (hasAdded != null) {
                            sortedAccounts.add(onDeviceAccountAndTokenPair)
                        }
                    }

                    accountsDataStore.updateData { store ->
                        store.copy(
                            accounts = sortedAccounts.map { (onDeviceAccount, token) ->
                                SignedInAccount(
                                    account = onDeviceAccount.toPbAccount(),
                                    accessToken = token.toPBAccessToken()
                                )
                            }
                        )
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }

            triggerNotificationWorker()
        }
    }

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
                AccountInstance(this, account)
            }
        }.asLiveData()
    }

    val unauthenticatedKtorClient by lazy {
        KtorClient(
            context = this,
            requireAuth = false,
            accessToken = null
        ).httpClient
    }

    companion object {
        private const val PER_PAGE = 16
        private const val MAX_SIZE_OF_PAGED_LIST = 1024

        val defaultPagingConfig = PagingConfig(
            pageSize = PER_PAGE,
            maxSize = MAX_SIZE_OF_PAGED_LIST
        )
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        accountManager.addOnAccountsUpdatedListener(
            accountListener,
            null,
            true
        )
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
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
                Timber.e(e)
            }
        }
    }

}