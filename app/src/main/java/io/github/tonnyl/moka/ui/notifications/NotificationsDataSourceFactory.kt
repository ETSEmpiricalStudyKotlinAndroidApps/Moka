package io.github.tonnyl.moka.ui.notifications

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.net.service.NotificationsService
import java.util.concurrent.Executor

class NotificationsDataSourceFactory(
        private val notificationsService: NotificationsService,
        private val executor: Executor
) : DataSource.Factory<String, Notification>() {

    val notificationsLiveData = MutableLiveData<NotificationsDataSource>()

    override fun create(): DataSource<String, Notification> = NotificationsDataSource(notificationsService, executor).apply {
        notificationsLiveData.postValue(this)
    }

}