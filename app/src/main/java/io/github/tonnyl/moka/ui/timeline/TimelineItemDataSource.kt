package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.MutableLiveData
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.db.dao.EventDao
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.service.EventsService
import io.github.tonnyl.moka.ui.*
import io.github.tonnyl.moka.util.PageLinks

class TimelineItemDataSource(
    private val eventsService: EventsService,
    private val eventDao: EventDao,
    var login: String,
    override val initial: MutableLiveData<Resource<List<Event>>>,
    override val previousOrNext: MutableLiveData<PagedResource<List<Event>>>
) : PageKeyedDataSourceWithLoadState<Event>() {

    init {
        require(login.isNotEmpty())
    }

    override fun doLoadInitial(params: LoadInitialParams<String>): InitialLoadResponse<Event> {
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

        val pl = PageLinks(response)

        return InitialLoadResponse(list, PreviousPageKey(pl.prev), NextPageKey(pl.next))
    }

    override fun doLoadAfter(params: LoadParams<String>): AfterLoadResponse<Event> {
        val response = eventsService.listPublicEventThatAUserHasReceivedByUrl(params.key)
            .execute()

        val list = response.body() ?: emptyList()

        if (list.isNotEmpty()) {
            eventDao.insertAll(list)
        }

        retry = null

        return AfterLoadResponse(list, NextPageKey(PageLinks(response).next))
    }

    override fun doLoadBefore(params: LoadParams<String>): BeforeLoadResponse<Event> {
        val response = eventsService.listPublicEventThatAUserHasReceivedByUrl(params.key)
            .execute()

        val list = response.body() ?: emptyList()

        if (list.isNotEmpty()) {
            eventDao.insertAll(list)
        }

        retry = null

        return BeforeLoadResponse(list, PreviousPageKey(PageLinks(response).prev))
    }

}