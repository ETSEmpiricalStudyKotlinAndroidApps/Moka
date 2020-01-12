package io.github.tonnyl.moka.ui.notifications

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.db.dao.NotificationDao
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.service.NotificationsService
import io.github.tonnyl.moka.util.PageLinks
import timber.log.Timber
import java.util.*

class NotificationsDataSource(
    private val notificationsService: NotificationsService,
    private val notificationDao: NotificationDao,
    private val initialLoadStatus: MutableLiveData<Resource<List<Notification>>>,
    private val previousNextStatus: MutableLiveData<PagedResource2<List<Notification>>>
) : PageKeyedDataSource<String, Notification>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, Notification>
    ) {
        Timber.d("loadInitial")

        initialLoadStatus.postValue(Resource.loading(null))

        // triggered by a refresh, we better execute sync
        try {
            val response = notificationsService.listNotifications(true, 1, params.requestedLoadSize)
                .execute()

            val list = (response.body() ?: Collections.emptyList()).map {
                it.hasDisplayed = true
                it
            }
            if (list.isNotEmpty()) {
                notificationDao.deleteAll()
                notificationDao.insert(list)
            }

            retry = null

            initialLoadStatus.postValue(Resource.success(list))

            val pl = PageLinks(response)
            callback.onResult(list, pl.prev, pl.next)
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadInitial(params, callback)
            }

            initialLoadStatus.postValue(Resource.error(e.message, null))
        }

    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, Notification>
    ) {
        Timber.d("loadAfter")

        previousNextStatus.postValue(
            PagedResource2(
                PagedResourceDirection.AFTER,
                Resource.loading(null)
            )
        )

        try {
            val response = notificationsService.listNotificationsByUrl(params.key)
                .execute()

            val list = (response.body() ?: Collections.emptyList()).map {
                it.hasDisplayed = true
                it
            }
            if (list.isNotEmpty()) {
                notificationDao.insert(list)
            }

            retry = null

            previousNextStatus.postValue(
                PagedResource2(PagedResourceDirection.AFTER, Resource.success(list))
            )

            val pl = PageLinks(response)
            callback.onResult(list, pl.next)
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadAfter(params, callback)
            }

            previousNextStatus.postValue(
                PagedResource2(PagedResourceDirection.AFTER, Resource.error(e.message, null))
            )
        }
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, Notification>
    ) {
        Timber.d("loadBefore")

        previousNextStatus.postValue(
            PagedResource2(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val response = notificationsService.listNotificationsByUrl(params.key)
                .execute()

            val list = (response.body() ?: Collections.emptyList()).map {
                it.hasDisplayed = true
                it
            }
            if (list.isNotEmpty()) {
                notificationDao.insert(list)
            }

            retry = null

            previousNextStatus.postValue(
                PagedResource2(PagedResourceDirection.BEFORE, Resource.success(list))
            )

            val pl = PageLinks(response)
            callback.onResult(list, pl.next)
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadBefore(params, callback)
            }

            previousNextStatus.postValue(
                PagedResource2(PagedResourceDirection.BEFORE, Resource.error(e.message, null))
            )
        }
    }

}