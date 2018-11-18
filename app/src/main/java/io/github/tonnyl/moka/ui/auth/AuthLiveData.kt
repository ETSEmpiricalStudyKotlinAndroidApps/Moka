package io.github.tonnyl.moka.ui.auth

import androidx.lifecycle.LiveData
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.data.AccessToken
import io.github.tonnyl.moka.net.Resource
import io.github.tonnyl.moka.net.RetrofitClient
import io.github.tonnyl.moka.net.Status
import io.github.tonnyl.moka.net.service.AccessTokenService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class AuthLiveData : LiveData<Resource<AccessToken>>() {

    private val service = RetrofitClient.createService(AccessTokenService::class.java, null)

    private lateinit var disposable: Disposable

    fun getAccessToken(code: String) {
        disposable = service.getAccessToken(
                RetrofitClient.GITHUB_GET_ACCESS_TOKEN_URL,
                BuildConfig.CLIENT_ID,
                BuildConfig.CLIENT_SECRET,
                code,
                RetrofitClient.GITHUB_AUTHORIZE_CALLBACK_URI
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    Resource(Status.SUCCESS, it, null)
                }
                .subscribe({
                    value = it
                }, {
                    Timber.e(it, "getAccessToken error: ${it.message}")
                })
    }

    override fun onInactive() {
        super.onInactive()
        if (disposable.isDisposed.not()) {
            disposable.dispose()
        }
    }

}