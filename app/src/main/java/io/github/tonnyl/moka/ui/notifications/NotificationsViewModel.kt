package io.github.tonnyl.moka.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.db.dao.NotificationDao
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.NotificationsService
import io.github.tonnyl.moka.ui.NetworkDatabaseSourceViewModel

class NotificationsViewModel(
    private val localSource: NotificationDao
) : NetworkDatabaseSourceViewModel<Notification>() {

    private val _loadStatusLiveData = MutableLiveData<PagedResource<List<Notification>>>()
    val loadStatusLiveData: LiveData<PagedResource<List<Notification>>>
        get() = _loadStatusLiveData

    override fun initLocalSource(): LiveData<PagedList<Notification>> {
        return LivePagedListBuilder(
            localSource.notificationsByDate(),
            pagingConfig
        ).build()
    }

    override fun initRemoteSource(): LiveData<PagedList<Notification>> {
        return LivePagedListBuilder(
            NotificationsDataSourceFactory(
                viewModelScope,
                RetrofitClient.createService(NotificationsService::class.java),
                localSource,
                _loadStatusLiveData
            ),
            pagingConfig
        ).build()
    }

}