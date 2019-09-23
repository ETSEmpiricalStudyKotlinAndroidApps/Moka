package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.db.dao.EventDao
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.service.EventsService
import io.github.tonnyl.moka.util.PageLinks
import timber.log.Timber
import java.util.*

class TimelineItemDataSource(
    private val eventsService: EventsService,
    private val eventDao: EventDao,
    var login: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<Event>>>,
    private val previousNextStatus: MutableLiveData<PagedResource2<List<Event>>>
) : PageKeyedDataSource<String, Event>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, Event>
    ) {
        Timber.d("loadInitial login: $login")

        if (login.isEmpty()) {
            return
        }

        initialLoadStatus.postValue(Resource.loading(null))

        // triggered by a refresh, we better execute sync
        try {
            val response = eventsService.listPublicEventThatAUserHasReceived(
                login,
                page = 1,
                perPage = params.requestedLoadSize
            ).execute()

            val list = response.body() ?: emptyList()
            if (list.isNotEmpty()) {
                eventDao.deleteAll()
                eventDao.insertAll(list)
            }

            retry = null

            initialLoadStatus.postValue(Resource.success(list))

            val pl = PageLinks(response)
            callback.onResult(list, pl.prev, pl.next)
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadInitial(params, callback)
            }

            initialLoadStatus.postValue(Resource.error(e.message, null))
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Event>) {
        Timber.d("loadAfter")

        previousNextStatus.postValue(
            PagedResource2(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val response = eventsService.listPublicEventThatAUserHasReceivedByUrl(params.key)
                .execute()

            val list = response.body() ?: Collections.emptyList()

            if (list.isNotEmpty()) {
                eventDao.insertAll(list)
            }

            retry = null

            val pl = PageLinks(response)
            callback.onResult(list, pl.next)
            previousNextStatus.postValue(
                PagedResource2(PagedResourceDirection.AFTER, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadAfter(params, callback)
            }

            previousNextStatus.postValue(
                PagedResource2(PagedResourceDirection.AFTER, Resource.error(e.message, null))
            )
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Event>) {
        Timber.d("loadBefore")

        previousNextStatus.postValue(
            PagedResource2(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val response = eventsService.listPublicEventThatAUserHasReceivedByUrl(params.key)
                .execute()

            val list = response.body() ?: Collections.emptyList()

            if (list.isNotEmpty()) {
                eventDao.insertAll(list)
            }

            retry = null

            val pl = PageLinks(response)
            callback.onResult(list, pl.prev)
            previousNextStatus.postValue(
                PagedResource2(PagedResourceDirection.BEFORE, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadBefore(params, callback)
            }

            previousNextStatus.postValue(
                PagedResource2(PagedResourceDirection.BEFORE, Resource.error(e.message, null))
            )
        }
    }

}