package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.service.EventsService
import kotlinx.coroutines.CoroutineScope

class TimelineDataSourceFactory(
    private val scope: CoroutineScope,
    private val eventsService: EventsService,
    var login: String,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<Event>>>
) : DataSource.Factory<String, Event>() {

    private val eventsLiveData = MutableLiveData<TimelineItemDataSource>()

    override fun create(): DataSource<String, Event> {
        return TimelineItemDataSource(scope, eventsService, login, loadStatusLiveData).apply {
            eventsLiveData.postValue(this)
        }
    }

    fun invalidate() {
        eventsLiveData.value?.let {
            it.login = login
            it.invalidate()
        }
    }

}