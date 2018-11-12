package io.github.tonnyl.moka.ui.repository

import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.RepositoryQuery
import io.github.tonnyl.moka.data.Repository2
import io.github.tonnyl.moka.data.Resource
import io.github.tonnyl.moka.data.Status
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RepositoryLiveData(
        private val login: String,
        private val name: String
) : LiveData<Resource<Repository2>>() {

    private val TAG = RepositoryLiveData::class.java.simpleName

    private val call = NetworkClient.apolloClient
            .query(RepositoryQuery.builder().login(login).repoName(name).build())
            .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
            .watcher()

    private val disposable = Rx2Apollo.from(call)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                if (response.hasErrors()) {
                    Resource<Repository2>(Status.ERROR, null, response.errors().first().message())
                } else {
                    Resource(Status.SUCCESS, Repository2.createFromRaw(response.data()), null)
                }
            }
            .subscribe({ data ->
                value = data
                Timber.d("data: $data")
            }, {
                Timber.e(it, "disposable error: ${it.message}")
            })

    init {
        value = Resource.loading(null)
    }

    override fun onInactive() {
        super.onInactive()
        if (disposable.isDisposed.not()) {
            disposable.dispose()
        }
    }

}