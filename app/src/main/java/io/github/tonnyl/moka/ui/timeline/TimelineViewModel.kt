package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.db.dao.EventDao
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.EventsService
import io.github.tonnyl.moka.ui.NetworkDatabaseSourceViewModel

class TimelineViewModel(
    private val localSource: EventDao
) : NetworkDatabaseSourceViewModel<Event>() {


    private val _loadStatusLiveData = MutableLiveData<PagedResource<List<Event>>>()
    val loadStatusLiveData: LiveData<PagedResource<List<Event>>>
        get() = _loadStatusLiveData


    override fun initLocalSource(): LiveData<PagedList<Event>> {
        return LivePagedListBuilder(
            localSource.eventsByCreatedAt(),
            pagingConfig
        ).build()
    }

    override fun initRemoteSource(): LiveData<PagedList<Event>> {
        return LivePagedListBuilder(
            TimelineDataSourceFactory(
                viewModelScope,
                RetrofitClient.createService(EventsService::class.java),
                localSource,
                login,
                _loadStatusLiveData
            ),
            pagingConfig
        ).build()
    }

}