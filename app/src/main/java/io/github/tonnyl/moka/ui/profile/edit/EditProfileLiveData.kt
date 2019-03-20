package io.github.tonnyl.moka.ui.profile.edit

import androidx.lifecycle.LiveData
import io.github.tonnyl.moka.net.Resource
import io.github.tonnyl.moka.net.RetrofitClient
import io.github.tonnyl.moka.net.Status
import io.github.tonnyl.moka.net.service.UserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class EditProfileLiveData : LiveData<Resource<Unit>>() {

    private val service = RetrofitClient.createService(UserService::class.java)

    private lateinit var disposable: Disposable

    override fun onInactive() {
        super.onInactive()
        if (this::disposable.isInitialized && disposable.isDisposed.not()) {
            disposable.dispose()
        }
    }

    fun updateUserInformation(
            name: String?,
            email: String,
            url: String?,
            company: String?,
            location: String?,
            bio: String?
    ) {
        val body = mapOf(Pair("name", name), Pair("email", email), Pair("url", url), Pair("company", company), Pair("location", location), Pair("bio", bio))
        value = Resource.loading(null)
        disposable = service.updateUseInformation(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    if (it.isSuccessful) {
                        Resource(Status.SUCCESS, Unit, null)
                    } else {
                        Resource(Status.ERROR, Unit, it.errorBody()?.string())
                    }
                }
                .subscribe({
                    value = it
                }, {
                    value = Resource.error(it.message ?: "", null)
                    Timber.e(it, "updateUserInformation error: ${it.message}")
                })
    }

}