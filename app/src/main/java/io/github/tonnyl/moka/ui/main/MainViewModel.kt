package io.github.tonnyl.moka.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.ViewerQuery
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val getViewerInfoCall = NetworkClient.apolloClient
            .query(ViewerQuery.builder().build())
            .httpCachePolicy(HttpCachePolicy.NETWORK_FIRST)
            .watcher()


    val login = MutableLiveData<String?>()
    val loginUserProfile = MutableLiveData<ViewerQuery.Data?>()

    fun getUserProfile() {
        val viewerInfoDisposable = Rx2Apollo.from(getViewerInfoCall)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ resp ->
                    Timber.d("get viewer info call success, resp = $resp")
                    val data = resp.data()
                    loginUserProfile.postValue(data)
                }, {
                    Timber.e(it, "get viewer info call error: ${it.message}")
                })
        compositeDisposable.add(viewerInfoDisposable)
    }

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
    }

}