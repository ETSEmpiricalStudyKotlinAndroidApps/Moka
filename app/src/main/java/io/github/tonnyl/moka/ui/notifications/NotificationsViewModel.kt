package io.github.tonnyl.moka.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.db.dao.NotificationDao
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.NotificationsService
import io.github.tonnyl.moka.ui.NetworkDatabaseSourceViewModel

class NotificationsViewModel(
    private val localSource: NotificationDao
) : NetworkDatabaseSourceViewModel<Notification>() {

    private val _initialLoadStatusLiveData = MutableLiveData<Resource<List<Notification>>>()
    val initialLoadStatusLiveData: LiveData<Resource<List<Notification>>>
        get() = _initialLoadStatusLiveData

    private val _previousNextLoadStatusLiveData =
        MutableLiveData<PagedResource2<List<Notification>>>()
    val previousNextLoadStatusLiveData: LiveData<PagedResource2<List<Notification>>>
        get() = _previousNextLoadStatusLiveData

    private lateinit var sourceFactory: NotificationsDataSourceFactory

    override fun initLocalSource(): LiveData<PagedList<Notification>> {
        return LivePagedListBuilder(
            localSource.notificationsByDate(),
            pagingConfig
        ).build()
    }

    override fun initRemoteSource(): LiveData<PagedList<Notification>> {
        sourceFactory = NotificationsDataSourceFactory(
            RetrofitClient.createService(NotificationsService::class.java),
            localSource,
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