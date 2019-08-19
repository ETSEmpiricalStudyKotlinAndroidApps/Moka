package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.db.dao.EventDao
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.service.EventsService
import kotlinx.coroutines.CoroutineScope

class TimelineDataSourceFactory(
    private val scope: CoroutineScope,
    private val eventsService: EventsService,
    private val eventDao: EventDao,
    var login: String,
    private val initialLoadStatusLiveData: MutableLiveData<Resource<List<Event>>>,
    private val previousNextStatusLiveData: MutableLiveData<PagedResource2<List<Event>>>
) : DataSource.Factory<String, Event>() {

    private var dataSource: TimelineItemDataSource? = null

    override fun create(): DataSource<String, Event> {
        return TimelineItemDataSource(
            scope,
            eventsService,
            eventDao,
            login,
            initialLoadStatusLiveData,
            previousNextStatusLiveData
        ).also {
            dataSource = it
        }
    }

    fun retryLoadPreviousNext() {
        dataSource?.retry?.invoke()
    }

}