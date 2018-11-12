package io.github.tonnyl.moka.ui.timeline

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.data.Status
import io.github.tonnyl.moka.net.EventsService
import io.github.tonnyl.moka.util.PageLinks
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class TimelineItemDataSource(
        private val eventsService: EventsService,
        private val login: String
) : PageKeyedDataSource<String, Event>() {

    private val TAG = javaClass.simpleName

    val status = MutableLiveData<Status>()

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Event>) {
        status.postValue(Status.LOADING)

        Log.d(TAG, "loadInitial")

        try {
            // triggered by a refresh, we better execute sync
            val response = eventsService.listPublicEventThatAUserHasReceived(login, page = 1, perPage = params.requestedLoadSize)
                    .execute()
            status.postValue(Status.SUCCESS)

            val pl = PageLinks(response)
            callback.onResult((response.body()
                    ?: emptyList()).toMutableList(), pl.prev, pl.next)
        } catch (ioe: IOException) {
            status.postValue(Status.ERROR)

            Log.e(TAG, "loadInitial params: $params error: ${ioe.message}")
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Event>) {
        Log.d(TAG, "loadAfter")
        eventsService.listPublicEventThatAUserHasReceivedByUrl(params.key)
                .enqueue(object : Callback<List<Event>> {

                    override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                        Log.e(TAG, "loadAfter params: ${params.key.toInt()} error: ${t.message}")
                    }

                    override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                        val pl = PageLinks(response)
                        Log.d(TAG, "PRE: ${pl.prev} next: ${pl.next}")

                        callback.onResult((response.body()
                                ?: emptyList()).toMutableList(), pl.next)
                    }

                })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Event>) {
        Log.d(TAG, "loadBefore")
        eventsService.listPublicEventThatAUserHasReceivedByUrl(params.key)
                .enqueue(object : Callback<List<Event>> {

                    override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                        Log.e(TAG, "loadBefore params: ${params.key.toInt() - 1} error: ${t.message}")
                    }

                    override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                        val pl = PageLinks(response)
                        Log.d(TAG, "PRE: ${pl.prev} next: ${pl.next}")

                        callback.onResult((response.body()
                                ?: emptyList()).toMutableList(), pl.prev)
                    }

                })
    }

}