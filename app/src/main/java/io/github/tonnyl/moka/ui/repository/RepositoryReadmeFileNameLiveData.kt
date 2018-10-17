package io.github.tonnyl.moka.ui.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.CurrentLevelTreeViewQuery
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.data.Resource
import io.github.tonnyl.moka.data.Status
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RepositoryReadmeFileNameLiveData(
        private val login: String,
        private val name: String,
        private val branchName: String
) : LiveData<Resource<Pair<String, String>>>() {

    private val TAG = javaClass.simpleName

    private val call = NetworkClient.apolloClient
            .query(CurrentLevelTreeViewQuery.builder().login(login).repoName(name).expression("$branchName:").build())
            .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
            .watcher()
    private val disposable = Rx2Apollo.from(call)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                if (response.hasErrors()) {
                    Resource(Status.ERROR, null, response.errors().first().message())
                } else {
                    val readmeFiles = response.data()?.repository()?.`object`()?.fragments()?.treeAbstract()?.entries()
                            ?.filter { it.name().toLowerCase().contains("readme") }
                    if (readmeFiles.isNullOrEmpty()) {
                        Resource(Status.SUCCESS, null, null)
                    } else {
                        val mdIndex = readmeFiles.indexOfFirst { it.name().endsWith(".md") }
                        if (mdIndex >= 0) {
                            return@map Resource(Status.SUCCESS,
                                    Pair("md", readmeFiles[mdIndex].name()),
                                    null)
                        }
                        val htmlIndex = readmeFiles.indexOfFirst { it.name().endsWith(".html") }
                        if (htmlIndex >= 0) {
                            return@map Resource(Status.SUCCESS,
                                    Pair("html", readmeFiles[htmlIndex].name()),
                                    null)
                        }
                        val plainIndex = readmeFiles.indexOfFirst { it.name().toLowerCase() == "readme" }
                        if (plainIndex >= 0) {
                            return@map Resource(Status.SUCCESS,
                                    Pair("plain", readmeFiles[plainIndex].name()),
                                    null)
                        }

                        return@map Resource(Status.SUCCESS,
                                null,
                                null)
                    }
                }
            }
            .subscribe({ data ->
                this.value = data
                Log.d(TAG, "data: $data")
            }, {
                Log.e(TAG, "disposable error: ${it.message}")
            })

    override fun onInactive() {
        super.onInactive()
        if (disposable.isDisposed.not()) {
            disposable.dispose()
        }
    }

}