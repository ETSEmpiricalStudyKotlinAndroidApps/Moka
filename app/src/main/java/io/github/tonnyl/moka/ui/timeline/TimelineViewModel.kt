package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.db.dao.EventDao
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.EventsService
import io.github.tonnyl.moka.ui.NetworkDatabaseSourceViewModel

class TimelineViewModel(
    private val localSource: EventDao
) : NetworkDatabaseSourceViewModel<Event>() {

    private val _initialLoadStatusLiveData = MutableLiveData<Resource<List<Event>>>()
    val initialLoadStatusLiveData: LiveData<Resource<List<Event>>>
        get() = _initialLoadStatusLiveData

    private val _previousNextLoadStatusLiveData = MutableLiveData<PagedResource2<List<Event>>>()
    val previousNextLoadStatusLiveData: LiveData<PagedResource2<List<Event>>>
        get() = _previousNextLoadStatusLiveData

    private lateinit var sourceFactory: TimelineDataSourceFactory

    override fun initLocalSource(): LiveData<PagedList<Event>> {
        return LivePagedListBuilder(
            localSource.eventsByCreatedAt(),
            pagingConfig
        ).build()
    }

    override fun initRemoteSource(): LiveData<PagedList<Event>> {
        sourceFactory = TimelineDataSourceFactory(
            viewModelScope,
            RetrofitClient.createService(EventsService::class.java),
            localSource,
            login,
            _initialLoadStatusLiveData,
            _previousNextLoadStatusLiveData
        )

        return LivePagedListBuilder(
            sourceFactory,
            pagingConfig
        ).build()
    }

    fun retryLoadPreviousNext() {
        sourceFactory.retryLoadPreviousNext()
    }

}