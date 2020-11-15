package io.github.tonnyl.moka.ui.auth

import android.accounts.AccountManager
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.network.service.AccessTokenService
import io.github.tonnyl.moka.network.service.UserService
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.auth.AuthEvent.FinishAndGo
import io.github.tonnyl.moka.util.insertNewAccount
import io.github.tonnyl.moka.util.updateOnAnyThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _authTokenAndUserResult =
        MutableLiveData<Resource<Pair<String, AuthenticatedUser>>>()
    val authTokenAndUserResult: LiveData<Resource<Pair<String, AuthenticatedUser>>>
        get() = _authTokenAndUserResult

    private val _event = MutableLiveData<Event<AuthEvent>>()
    val event: LiveData<Event<AuthEvent>>
        get() = _event

    private val service: AccessTokenService by lazy(LazyThreadSafetyMode.NONE) {
        RetrofitClient.createService(AccessTokenService::class.java)
    }

    private val accountManager by lazy(LazyThreadSafetyMode.NONE) {
        AccountManager.get(getApplication())
    }

    fun getAccessToken(code: String, state: String) {
        viewModelScope.launch {
            try {
                _authTokenAndUserResult.value = Resource(Status.LOADING, null, null)

                val tokenResult = withContext(Dispatchers.IO) {
                    service.getAccessToken(
                        RetrofitClient.GITHUB_GET_ACCESS_TOKEN_URL,
                        BuildConfig.CLIENT_ID,
                        BuildConfig.CLIENT_SECRET,
                        code,
                        RetrofitClient.GITHUB_AUTHORIZE_CALLBACK_URI,
                        state
                    )
                }

                RetrofitClient.accessToken.set(tokenResult.body()?.accessToken)
                val userService = RetrofitClient.createService(UserService::class.java)

                val authUserResult = withContext(Dispatchers.IO) {
                    userService.getAuthenticatedUser()
                }

                val authenticatedUser = authUserResult.body()
                val token = tokenResult.body()?.accessToken

                if (authenticatedUser != null
                    && !token.isNullOrEmpty()
                ) {
                    accountManager.insertNewAccount(token, authenticatedUser)

                    _authTokenAndUserResult.value =
                        Resource(Status.SUCCESS, Pair(token, authenticatedUser), null)

                    _event.updateOnAnyThread(Event(FinishAndGo))
                } else {
                    _authTokenAndUserResult.value =
                        Resource(Status.ERROR, null, authUserResult.message())
                }
            } catch (e: Exception) {
                _authTokenAndUserResult.value = Resource(Status.ERROR, null, e.message)

                Timber.e(e)
            }
        }
    }

}