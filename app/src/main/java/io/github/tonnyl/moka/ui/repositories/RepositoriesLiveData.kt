package io.github.tonnyl.moka.ui.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.OwnedRepositoriesQuery
import io.github.tonnyl.moka.StarredRepositoriesQuery
import io.github.tonnyl.moka.data.RepositoryAbstract
import io.github.tonnyl.moka.data.Resource
import io.github.tonnyl.moka.data.Status
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RepositoriesLiveData(
        private val login: String,
        private val repositoryType: String
) : LiveData<Resource<List<RepositoryAbstract>>>() {

    private val TAG = RepositoriesLiveData::class.java.simpleName

    private val ownedRepositoryCall = NetworkClient.apolloClient
            .query(OwnedRepositoriesQuery.builder().login(login).build())
            .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
            .watcher()
    private val starredRepositoryCall = NetworkClient.apolloClient
            .query(StarredRepositoriesQuery.builder().login(login).build())
            .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
            .watcher()

    private var ownedRepositoryDisposable: Disposable? = null
    private var starredRepositoryDisposable: Disposable? = null

    init {
        value = Resource.loading(null)

        if (repositoryType == RepositoriesFragment.REPOSITORY_TYPE_OWNED) {
            ownedRepositoryDisposable = Rx2Apollo.from(ownedRepositoryCall)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { response ->
                        val list = mutableListOf<RepositoryAbstract>()
                        val user = response.data()?.user()
                        user?.repositories()?.nodes()?.forEach { node ->
                            list.add(RepositoryAbstract.createFromOwnedRepositoryDataNode(node))
                        }
                        val status = if (response.hasErrors()) Status.ERROR else Status.SUCCESS

                        Resource<List<RepositoryAbstract>>(status, list, null)
                    }
                    .subscribe({ data ->
                        value = data
                        Log.d(TAG, "data: $data")
                    }, {
                        Log.e(TAG, "disposable error: ${it.message}")
                    })
        } else if (repositoryType == RepositoriesFragment.REPOSITORY_TYPE_STARS) {
            starredRepositoryDisposable = Rx2Apollo.from(starredRepositoryCall)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { response ->
                        val list = mutableListOf<RepositoryAbstract>()
                        val user = response.data()?.user()
                        user?.starredRepositories()?.nodes()?.forEach { node ->
                            list.add(RepositoryAbstract.createFromStarredRepositoryDataNode(node))
                        }
                        val status = if (response.hasErrors()) Status.ERROR else Status.SUCCESS

                        Resource<List<RepositoryAbstract>>(status, list, null)
                    }
                    .subscribe({ data ->
                        value = data
                        Log.d(TAG, "data: $data")
                    }, {
                        Log.e(TAG, "disposable error: ${it.message}")
                    })
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (ownedRepositoryDisposable?.isDisposed?.not() == true) {
            ownedRepositoryDisposable?.dispose()
        }
        if (starredRepositoryDisposable?.isDisposed?.not() == true) {
            starredRepositoryDisposable?.dispose()
        }
    }

}