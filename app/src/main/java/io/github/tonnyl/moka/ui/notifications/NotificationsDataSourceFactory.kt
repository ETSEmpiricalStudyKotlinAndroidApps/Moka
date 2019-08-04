package io.github.tonnyl.moka.ui.notifications

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.db.dao.NotificationDao
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.service.NotificationsService
import kotlinx.coroutines.CoroutineScope

class NotificationsDataSourceFactory(
    private val coroutineScope: CoroutineScope,
    private val notificationsService: NotificationsService,
    private val notificationDao: NotificationDao,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<Notification>>>
) : DataSource.Factory<String, Notification>() {

    override fun create(): DataSource<String, Notification> {
        return NotificationsDataSource(
            coroutineScope,
            notificationsService,
            notificationDao,
            loadStatusLiveData
        )
    }

}