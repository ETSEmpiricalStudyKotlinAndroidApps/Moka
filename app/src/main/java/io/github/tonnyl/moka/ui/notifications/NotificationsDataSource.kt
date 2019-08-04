package io.github.tonnyl.moka.ui.notifications

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.db.dao.NotificationDao
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.service.NotificationsService
import io.github.tonnyl.moka.util.PageLinks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class NotificationsDataSource(
    private val coroutineScope: CoroutineScope,
    private val notificationsService: NotificationsService,
    private val notificationDao: NotificationDao,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<Notification>>>
) : PageKeyedDataSource<String, Notification>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, Notification>
    ) {
        Timber.d("loadInitial")

        loadStatusLiveData.postValue(PagedResource(initial = Resource.loading(null)))

        // triggered by a refresh, we better execute sync
        try {
            val response = notificationsService.listNotifications(true, 1, params.requestedLoadSize)
                .execute()

            val list = response.body() ?: Collections.emptyList()
            if (list.isNotEmpty()) {
                notificationDao.insert(list)
            }

            retry = null

            loadStatusLiveData.postValue(PagedResource(initial = Resource.success(list)))

            val pl = PageLinks(response)
            callback.onResult(list, pl.prev, pl.next)
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadInitial(params, callback)
            }

            loadStatusLiveData.postValue(PagedResource(initial = Resource.error(e.message, null)))
        }

    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, Notification>
    ) {
        Timber.d("loadAfter")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(after = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    notificationsService.listNotificationsByUrl(params.key)
                }

                val list = response.body() ?: Collections.emptyList()
                if (list.isNotEmpty()) {
                    notificationDao.insert(list)
                }

                retry = null

                loadStatusLiveData.value = PagedResource(after = Resource.success(list))

                val pl = PageLinks(response)
                callback.onResult(list, pl.next)
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.value = PagedResource(after = Resource.error(e.message, null))
            }
        }
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, Notification>
    ) {
        Timber.d("loadBefore")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(before = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    notificationsService.listNotificationsByUrl(params.key)
                }

                val list = response.body() ?: Collections.emptyList()
                if (list.isNotEmpty()) {
                    notificationDao.insert(list)
                }

                retry = null

                loadStatusLiveData.value = PagedResource(before = Resource.success(list))

                val pl = PageLinks(response)
                callback.onResult(list, pl.next)
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadBefore(params, callback)
                }

                loadStatusLiveData.value = PagedResource(before = Resource.error(e.message, null))
            }
        }
    }

}