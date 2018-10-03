package io.github.tonnyl.moka.ui.timeline

import android.util.Log
import androidx.lifecycle.LiveData
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.net.EventsService
import io.github.tonnyl.moka.net.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

open class EventsLiveData(
        private val login: String
) : LiveData<Response<List<Event>>>() {

    private val service = RetrofitClient.createService(EventsService::class.java, null)
    private val disposable = service.listEventThatAUserHasReceived(login)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data ->
                value = data
            }, {
                Log.e("EventsLiveData", "disposable error: ${it.message}")
            }, {

            })

    override fun onInactive() {
        super.onInactive()
        if (disposable.isDisposed.not()) {
            disposable.dispose()
        }
    }

}