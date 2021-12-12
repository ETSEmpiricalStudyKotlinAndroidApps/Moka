package io.github.tonnyl.moka.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.extension.toPBAccessToken
import io.github.tonnyl.moka.data.extension.toPbAccount
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.util.insertNewAccount
import io.github.tonnyl.moka.util.updateOnAnyThread
import io.tonnyl.moka.common.data.AuthenticatedUser
import io.tonnyl.moka.common.network.KtorClient
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.common.network.api.AccessTokenApi
import io.tonnyl.moka.common.network.api.UserApi
import io.tonnyl.moka.common.store.data.SignedInAccount
import io.tonnyl.moka.common.ui.auth.AuthEvent
import io.tonnyl.moka.common.ui.auth.AuthEvent.FinishAndGo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _authTokenAndUserResult =
        MutableLiveData<Resource<Pair<String, AuthenticatedUser>>>()
    val authTokenAndUserResult: LiveData<Resource<Pair<String, AuthenticatedUser>>>
        get() = _authTokenAndUserResult

    private val _event = MutableLiveData<Event<AuthEvent>>()
    val event: LiveData<Event<AuthEvent>>
        get() = _event

    @ExperimentalSerializationApi
    fun getAccessToken(code: String, state: String) {
        viewModelScope.launch {
            try {
                _authTokenAndUserResult.value = Resource(Status.LOADING, null, null)

                val accessTokenResp = withContext(Dispatchers.IO) {
                    AccessTokenApi(ktorClient = KtorClient.unauthenticatedKtorClient).getAccessToken(
                        clientId = BuildConfig.CLIENT_ID,
                        clientSecret = BuildConfig.CLIENT_SECRET,
                        code = code,
                        redirectUrl = KtorClient.GITHUB_AUTHORIZE_CALLBACK_URI,
                        state = state
                    )
                }

                val authenticatedUserResp = withContext(Dispatchers.IO) {
                    UserApi(ktorClient = KtorClient.unauthenticatedKtorClient)
                        .getAuthenticatedUser(accessToken = accessTokenResp.accessToken)
                }

                getApplication<MokaApp>().accountManager.insertNewAccount(
                    token = accessTokenResp,
                    user = authenticatedUserResp
                )

                getApplication<MokaApp>().accountsDataStore.updateData { signedInAccounts ->
                    val newAccounts = signedInAccounts.accounts.toMutableList()
                    val existingAccountIndex = newAccounts.indexOfFirst {
                        it.account.id == authenticatedUserResp.id
                    }
                    if (existingAccountIndex >= 0) {
                        newAccounts.removeAt(existingAccountIndex)
                        newAccounts.add(
                            existingAccountIndex,
                            SignedInAccount(
                                accessToken = accessTokenResp.toPBAccessToken(),
                                account = authenticatedUserResp.toPbAccount()
                            )
                        )
                    } else {
                        newAccounts.add(
                            0,
                            SignedInAccount(
                                accessToken = accessTokenResp.toPBAccessToken(),
                                account = authenticatedUserResp.toPbAccount()
                            )
                        )
                    }
                    signedInAccounts.copy(accounts = newAccounts)
                }
                _authTokenAndUserResult.value =
                    Resource(
                        Status.SUCCESS,
                        Pair(accessTokenResp.accessToken, authenticatedUserResp),
                        null
                    )

                _event.updateOnAnyThread(Event(FinishAndGo))
            } catch (e: Exception) {
                _authTokenAndUserResult.value = Resource(Status.ERROR, null, e)

                logcat(priority = LogPriority.ERROR) { e.asLog() }
            }
        }
    }

}