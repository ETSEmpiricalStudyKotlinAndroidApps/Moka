package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.IssueQuery
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.data.IssueGraphQL
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class IssueLiveData(
        private val owner: String,
        private val name: String,
        private val number: Int
) : LiveData<IssueGraphQL?>() {

    private val call = NetworkClient.apolloClient
            .query(IssueQuery.builder()
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
                IssueGraphQL.createFromRaw(it.data()?.repository()?.issue())
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