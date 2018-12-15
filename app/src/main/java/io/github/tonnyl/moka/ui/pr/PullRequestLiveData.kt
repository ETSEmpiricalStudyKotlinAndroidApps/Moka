package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.PullRequestQuery
import io.github.tonnyl.moka.data.PullRequestGraphQL
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PullRequestLiveData(
        private val owner: String,
        private val name: String,
        private val number: Int
) : LiveData<PullRequestGraphQL?>() {

    private val call = NetworkClient.apolloClient
            .query(PullRequestQuery.builder()
                    .owner(owner)
                    .name(name)
                    .number(number)
                    .build())
            .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
            .watcher()

    private val disposable = Rx2Apollo.from(call)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                PullRequestGraphQL.createFromRaw(it.data()?.repository()?.pullRequest())
            }
            .subscribe({ data ->
                value = data
                Timber.d("disposable data: $data")
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