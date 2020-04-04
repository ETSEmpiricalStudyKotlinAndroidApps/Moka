package io.github.tonnyl.moka.ui.inbox

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.db.dao.NotificationDao
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.service.NotificationsService

class InboxDataSourceFactory(
    private val notificationsService: NotificationsService,
    private val notificationDao: NotificationDao,
    private val initialLoadStatus: MutableLiveData<Resource<List<Notification>>>,
    private val previousNextStatus: MutableLiveData<PagedResource<List<Notification>>>
) : DataSource.Factory<String, Notification>() {

    private var dataSource: InboxDataSource? = null

    override fun create(): DataSource<String, Notification> {
        return InboxDataSource(
            notificationsService,
            notificationDao,
            initialLoadStatus,
            previousNextStatus
        ).also {
            dataSource = it
        }
    }

    fun retryLoadPreviousNext() {
        dataSource?.retry?.invoke()
    }

}