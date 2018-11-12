package io.github.tonnyl.moka.ui.profile

import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.PinnedRepositoriesQuery
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PinnedRepositoriesLiveData(
        private val login: String
) : LiveData<Response<PinnedRepositoriesQuery.Data>>() {

    private val TAG = PinnedRepositoriesLiveData::class.java.simpleName

    private val call = NetworkClient.apolloClient
            .query(PinnedRepositoriesQuery.builder().login(login).build())
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