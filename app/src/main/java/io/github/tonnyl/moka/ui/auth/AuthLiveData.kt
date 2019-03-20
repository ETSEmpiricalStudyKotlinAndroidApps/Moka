package io.github.tonnyl.moka.ui.auth

import androidx.lifecycle.LiveData
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.net.Resource
import io.github.tonnyl.moka.net.RetrofitClient
import io.github.tonnyl.moka.net.Status
import io.github.tonnyl.moka.net.service.AccessTokenService
import io.github.tonnyl.moka.net.service.UserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class AuthLiveData : LiveData<Resource<Pair<String, AuthenticatedUser>>>() {

    private val service = RetrofitClient.createService(AccessTokenService::class.java)

    private lateinit var disposable: Disposable

    fun getAccessToken(code: String, state: String) {
        value = Resource(Status.LOADING, null, null)

        disposable = service.getAccessToken(
                RetrofitClient.GITHUB_GET_ACCESS_TOKEN_URL,
                BuildConfig.CLIENT_ID,
                BuildConfig.CLIENT_SECRET,
                code,
                RetrofitClient.GITHUB_AUTHORIZE_CALLBACK_URI,
                state
        ).flatMap {
            RetrofitClient.lastToken = it.body()?.accessToken

            val userService = RetrofitClient.createService(UserService::class.java)
            userService.getAuthenticatedUser()
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val body = it.body()
                    value = if (it.isSuccessful && body != null) {
                        Resource(Status.SUCCESS, Pair(RetrofitClient.lastToken
                                ?: "", body), null)
                    } else {
                        Resource(Status.ERROR, null, it.message())
                    }
                }, {
                    Timber.e(it, "getAccessToken error: ${it.message}")
                })
    }

    override fun onInactive() {
        super.onInactive()
        if (this::disposable.isInitialized && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

}