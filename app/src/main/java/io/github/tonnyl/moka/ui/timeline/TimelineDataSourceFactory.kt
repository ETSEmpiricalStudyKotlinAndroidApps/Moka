package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.net.service.EventsService

class TimelineDataSourceFactory(
        private val eventsService: EventsService,
        private val login: String
) : DataSource.Factory<String, Event>() {

    private val eventsLiveData = MutableLiveData<TimelineItemDataSource>()

    override fun create(): DataSource<String, Event> = TimelineItemDataSource(eventsService, login).apply {
        eventsLiveData.postValue(this)
    }

    fun invalidate() {
        eventsLiveData.value?.invalidate()
    }

}