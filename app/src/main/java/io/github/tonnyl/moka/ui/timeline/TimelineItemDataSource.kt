package io.github.tonnyl.moka.ui.timeline

import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.net.service.EventsService
import io.github.tonnyl.moka.util.PageLinks
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

class TimelineItemDataSource(
        private val eventsService: EventsService,
        private val login: String
) : PageKeyedDataSource<String, Event>() {

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Event>) {
        try {
            // triggered by a refresh, we better execute sync
            val response = eventsService.listPublicEventThatAUserHasReceived(login, page = 1, perPage = params.requestedLoadSize)
                    .execute()

            val pl = PageLinks(response)
            callback.onResult(response.body() ?: emptyList(), pl.prev, pl.next)
        } catch (ioe: IOException) {
            Timber.e(ioe, "loadInitial params: $params error: ${ioe.message}")
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Event>) {
        eventsService.listPublicEventThatAUserHasReceivedByUrl(params.key)
                .enqueue(object : Callback<List<Event>> {

                    override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                        Timber.e(t, "loadAfter params: ${params.key} error: ${t.message}")
                    }

                    override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                        val pl = PageLinks(response)
                        callback.onResult((response.body()
                                ?: emptyList()).toMutableList(), pl.next)
                    }

                })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Event>) {
        eventsService.listPublicEventThatAUserHasReceivedByUrl(params.key)
                .enqueue(object : Callback<List<Event>> {

                    override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                        Timber.e(t, "loadBefore params: ${params.key} error: ${t.message}")
                    }

                    override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                        val pl = PageLinks(response)
                        Timber.d("PRE: ${pl.prev} next: ${pl.next}")

                        callback.onResult((response.body()
                                ?: emptyList()).toMutableList(), pl.prev)
                    }

                })
    }

}