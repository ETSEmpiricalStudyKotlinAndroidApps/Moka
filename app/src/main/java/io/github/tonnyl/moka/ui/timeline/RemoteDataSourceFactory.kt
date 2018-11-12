package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.net.EventsService

class RemoteDataSourceFactory(
        private val eventsService: EventsService,
        private val login: String
) : DataSource.Factory<String, Event>() {

    private val eventsLiveData = MutableLiveData<RemoteItemDataSource>()

    override fun create(): DataSource<String, Event> = RemoteItemDataSource(eventsService, login).apply {
        eventsLiveData.postValue(this)
    }

}