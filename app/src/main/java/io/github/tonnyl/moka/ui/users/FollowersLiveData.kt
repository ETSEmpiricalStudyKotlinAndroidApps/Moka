package io.github.tonnyl.moka.ui.users

import android.util.Log
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.FollowersQuery
import io.github.tonnyl.moka.NetworkClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

open class FollowersLiveData(
        private val login: String
) : LiveData<Response<FollowersQuery.Data>>() {

    private val TAG = FollowersLiveData::class.java.simpleName

    private val call = NetworkClient.apolloClient
            .query(FollowersQuery.builder().login(login).build())
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