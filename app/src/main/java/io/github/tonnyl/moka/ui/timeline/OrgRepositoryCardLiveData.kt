package io.github.tonnyl.moka.ui.timeline

import android.util.Log
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.OrgRepositoryCardInfoQuery
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

open class OrgRepositoryCardLiveData(
        private val login: String,
        private val repositoryName: String
) : LiveData<Response<OrgRepositoryCardInfoQuery.Data>>() {

    val TAG = OrgRepositoryCardLiveData::class.java.simpleName

    private val call = NetworkClient.apolloClient
            .query(OrgRepositoryCardInfoQuery.builder().login(login).repositoryName(repositoryName).build())
            .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
            .watcher()
    private val disposable = Rx2Apollo.from(call)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data ->
                value = data
                Log.d(TAG, "data: ${data.data()}")
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