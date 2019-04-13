package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.data.PagedResource
import io.github.tonnyl.moka.net.Resource
import io.github.tonnyl.moka.net.service.EventsService
import io.github.tonnyl.moka.util.PageLinks
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class TimelineItemDataSource(
        private val eventsService: EventsService,
        var login: String,
        private val loadStatusLiveData: MutableLiveData<PagedResource<List<Event>>>
) : PageKeyedDataSource<String, Event>() {

    var retry: (() -> Any)? = null

    private var call: Call<List<Event>>? = null

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Event>) {
        Timber.d("loadInitial")

        loadStatusLiveData.postValue(PagedResource(initial = Resource.loading(null)))

        call = eventsService.listPublicEventThatAUserHasReceived(login, page = 1, perPage = params.requestedLoadSize)

        call?.enqueue(object : Callback<List<Event>> {

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Timber.e(t)

                retry = {
                    loadInitial(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(initial = Resource.error(t.message, null)))
            }

            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                val list = response.body() ?: emptyList()

                val pl = PageLinks(response)
                callback.onResult(list, pl.prev, pl.next)

                retry = null

                loadStatusLiveData.postValue(PagedResource(initial = Resource.success(list)))
            }

        })
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Event>) {
        Timber.d("loadAfter")

        loadStatusLiveData.postValue(PagedResource(after = Resource.loading(null)))

        call = eventsService.listPublicEventThatAUserHasReceivedByUrl(params.key)

        call?.enqueue(object : Callback<List<Event>> {

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Timber.e(t)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(after = Resource.error(t.message, null)))
            }

            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                val list = response.body() ?: emptyList()

                val pl = PageLinks(response)
                callback.onResult(list, pl.next)

                retry = null

                loadStatusLiveData.postValue(PagedResource(after = Resource.success(list)))
            }

        })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Event>) {
        call = eventsService.listPublicEventThatAUserHasReceivedByUrl(params.key)

        call?.enqueue(object : Callback<List<Event>> {

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Timber.e(t)

                retry = {
                    loadBefore(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(before = Resource.error(t.message, null)))
            }

            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                val list = response.body() ?: emptyList()

                val pl = PageLinks(response)
                callback.onResult(list, pl.prev)

                retry = null

                loadStatusLiveData.postValue(PagedResource(before = Resource.success(list)))
            }

        })
    }

    override fun invalidate() {
        super.invalidate()

        if (call?.isCanceled == false) {
            call?.cancel()
        }
    }

}