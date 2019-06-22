package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.service.EventsService
import io.github.tonnyl.moka.util.PageLinks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class TimelineItemDataSource(
    private val coroutineScope: CoroutineScope,
    private val eventsService: EventsService,
    var login: String,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<Event>>>
) : PageKeyedDataSource<String, Event>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Event>) {
        Timber.d("loadInitial")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(initial = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    eventsService.listPublicEventThatAUserHasReceived(
                        login,
                        page = 1,
                        perPage = params.requestedLoadSize
                    )
                }

                val list = response.body() ?: emptyList()

                val pl = PageLinks(response)
                callback.onResult(list, pl.prev, pl.next)

                retry = null

                loadStatusLiveData.value = PagedResource(initial = Resource.success(list))
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadInitial(params, callback)
                }

                loadStatusLiveData.value = PagedResource(initial = Resource.error(e.message, null))
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Event>) {
        Timber.d("loadAfter")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(after = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    eventsService.listPublicEventThatAUserHasReceivedByUrl(params.key)
                }

                val list = response.body() ?: emptyList()

                val pl = PageLinks(response)
                callback.onResult(list, pl.next)

                retry = null

                loadStatusLiveData.value = PagedResource(after = Resource.success(list))
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.value = PagedResource(after = Resource.error(e.message, null))
            }
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Event>) {
        Timber.d("loadBefore")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(before = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    eventsService.listPublicEventThatAUserHasReceivedByUrl(params.key)
                }

                val list = response.body() ?: emptyList()

                val pl = PageLinks(response)
                callback.onResult(list, pl.prev)

                retry = null

                loadStatusLiveData.value = PagedResource(before = Resource.success(list))
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadBefore(params, callback)
                }

                loadStatusLiveData.value = PagedResource(before = Resource.error(e.message, null))
            }
        }
    }

}