package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.data.PagedResource
import io.github.tonnyl.moka.net.service.EventsService

class TimelineDataSourceFactory(
        private val eventsService: EventsService,
        var login: String,
        private val loadStatusLiveData: MutableLiveData<PagedResource<List<Event>>>
) : DataSource.Factory<String, Event>() {

    private val eventsLiveData = MutableLiveData<TimelineItemDataSource>()

    override fun create(): DataSource<String, Event> = TimelineItemDataSource(eventsService, login, loadStatusLiveData).apply {
        eventsLiveData.postValue(this)
    }

    fun invalidate() {
        eventsLiveData.value?.let {
            it.login = login
            it.invalidate()
        }
    }

}