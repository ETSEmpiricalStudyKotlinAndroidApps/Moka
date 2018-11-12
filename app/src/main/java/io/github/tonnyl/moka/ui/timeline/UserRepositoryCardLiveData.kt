package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.UserRepositoryCardInfoQuery
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

open class UserRepositoryCardLiveData(
        private val login: String,
        private val repositoryName: String
) : LiveData<Response<UserRepositoryCardInfoQuery.Data>>() {

    private val TAG = UserRepositoryCardLiveData::class.java.simpleName

    private val call = NetworkClient.apolloClient
            .query(UserRepositoryCardInfoQuery.builder().login(login).repositoryName(repositoryName).build())
            .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
            .watcher()
    private val disposable = Rx2Apollo.from(call)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data ->
                value = data
                Timber.d("data: ${data.data()}")
            }, {
                Timber.e(it, "disposable error: ${it.message}")
            })

    override fun onInactive() {
        super.onInactive()
        if (disposable.isDisposed.not()) {
            disposable.dispose()
        }
    }
}