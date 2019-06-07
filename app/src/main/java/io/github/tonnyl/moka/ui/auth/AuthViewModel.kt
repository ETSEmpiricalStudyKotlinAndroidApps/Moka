package io.github.tonnyl.moka.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.network.service.AccessTokenService
import io.github.tonnyl.moka.network.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {

    private val _authTokenAndUserResult = MutableLiveData<Resource<Pair<String, AuthenticatedUser>>>()
    val authTokenAndUserResult: LiveData<Resource<Pair<String, AuthenticatedUser>>>
        get() = _authTokenAndUserResult

    private val service: AccessTokenService by lazy(LazyThreadSafetyMode.NONE) {
        RetrofitClient.createService(AccessTokenService::class.java)
    }

    fun getAccessToken(code: String, state: String) {
        _authTokenAndUserResult.value = Resource(Status.LOADING, null, null)

        viewModelScope.launch {
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

            RetrofitClient.lastToken = tokenResult.body()?.accessToken
            val userService = RetrofitClient.createService(UserService::class.java)

            val authUserResult = withContext(Dispatchers.IO) {
                userService.getAuthenticatedUser()
            }

            val body = authUserResult.body()
            _authTokenAndUserResult.value = if (authUserResult.isSuccessful && body != null) {
                Resource(Status.SUCCESS, Pair(RetrofitClient.lastToken
                        ?: "", body), null)
            } else {
                Resource(Status.ERROR, null, authUserResult.message())
            }
        }
    }

}