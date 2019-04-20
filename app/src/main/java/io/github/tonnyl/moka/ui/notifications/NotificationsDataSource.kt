package io.github.tonnyl.moka.ui.notifications

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.service.NotificationsService
import io.github.tonnyl.moka.util.PageLinks
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

class NotificationsDataSource(
        private val notificationsService: NotificationsService,
        private val loadStatusLiveData: MutableLiveData<PagedResource<List<Notification>>>
) : PageKeyedDataSource<String, Notification>() {

    var retry: (() -> Any)? = null

    private var call: Call<List<Notification>>? = null

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Notification>) {
        Timber.d("loadInitial")

        loadStatusLiveData.postValue(PagedResource(initial = Resource.loading(null)))

        call = notificationsService.listNotifications(true, 1, params.requestedLoadSize)

        call?.enqueue(object : Callback<List<Notification>> {

            override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                Timber.e(t)

                retry = {
                    loadInitial(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(initial = Resource.error(t.message, null)))
            }

            override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                val list = response.body() ?: Collections.emptyList()

                val pl = PageLinks(response)
                callback.onResult(list, pl.prev, pl.next)

                retry = null

                loadStatusLiveData.postValue(PagedResource(initial = Resource.success(list)))
            }

        })
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Notification>) {
        Timber.d("loadAfter")

        loadStatusLiveData.postValue(PagedResource(after = Resource.loading(null)))

        call = notificationsService.listNotificationsByUrl(params.key)

        call?.enqueue(object : Callback<List<Notification>> {

            override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                Timber.e(t)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(after = Resource.error(t.message, null)))
            }

            override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                val list = response.body() ?: Collections.emptyList()

                val pl = PageLinks(response)
                callback.onResult(list, pl.next)

                retry = null

                loadStatusLiveData.postValue(PagedResource(after = Resource.success(list)))
            }

        })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Notification>) {
        Timber.d("loadBefore")

        loadStatusLiveData.postValue(PagedResource(before = Resource.loading(null)))

        call = notificationsService.listNotificationsByUrl(params.key)

        call?.enqueue(object : Callback<List<Notification>> {

            override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                Timber.e(t)

                retry = {
                    loadBefore(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(before = Resource.error(t.message, null)))
            }

            override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                val list = response.body() ?: emptyList()

                val pl = PageLinks(response)
                callback.onResult(list, pl.prev)

                retry = null

                loadStatusLiveData.postValue(PagedResource(before = Resource.success(list)))
            }

        })
    }

    override fun invalidate() {
        super.invalidate()

        if (call?.isCanceled == false) {
            call?.cancel()
        }
    }

}