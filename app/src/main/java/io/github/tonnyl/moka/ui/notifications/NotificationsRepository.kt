package io.github.tonnyl.moka.ui.notifications

import androidx.lifecycle.Transformations.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import io.github.tonnyl.moka.data.Listing
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.net.service.NotificationsService
import java.util.concurrent.Executor

class NotificationsRepository(
        private val notificationsService: NotificationsService,
        private val networkExecutor: Executor
) {

    fun notifications(pageSize: Int): Listing<Notification> {
        val sourceFactory = NotificationsDataSourceFactory(notificationsService, networkExecutor)

        val livePagedList = sourceFactory.toLiveData(
                fetchExecutor = networkExecutor,
                config = PagedList.Config.Builder()
                        .setPageSize(pageSize)
                        .setInitialLoadSizeHint(pageSize)
                        .setEnablePlaceholders(true)
                        .build()
        )

        val refreshState = switchMap(sourceFactory.notificationsLiveData) {
            it.initialLoad
        }

        return Listing(
                pagedList = livePagedList,
                networkState = switchMap(sourceFactory.notificationsLiveData) {
                    it.networkState
                },
                retry = {
                    sourceFactory.notificationsLiveData.value?.retryAllFailed()
                },
                refresh = {
                    sourceFactory.notificationsLiveData.value?.invalidate()
                },
                refreshState = refreshState
        )
    }

}