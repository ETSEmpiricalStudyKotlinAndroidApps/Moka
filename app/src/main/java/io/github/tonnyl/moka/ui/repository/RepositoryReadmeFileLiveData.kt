package io.github.tonnyl.moka.ui.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.FileContentQuery
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.data.Resource
import io.github.tonnyl.moka.data.Status
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RepositoryReadmeFileLiveData(
        private val login: String,
        private val name: String,
        private val expression: String
) : LiveData<Resource<String>>() {

    private val TAG = javaClass.simpleName

    private val call = NetworkClient.apolloClient
            .query(FileContentQuery.builder().login(login).repoName(name).expression(expression).build())
            .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
            .watcher()

    private val disposable = Rx2Apollo.from(call)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                if (response.hasErrors()) {
                    Resource(Status.ERROR, null, response.errors().joinToString())
                } else {
                    Resource(Status.SUCCESS,
                            response.data()?.repository()?.`object`()?.fragments()?.fileTextAbstract()?.text(),
                            null)
                }
            }
            .subscribe({
                this.value = it
            }, {
                Log.d(TAG, "fetchReadmeContent error: ${it.message}")
            })

    override fun onInactive() {
        super.onInactive()
        if (disposable.isDisposed.not()) {
            disposable.dispose()
        }
    }

}