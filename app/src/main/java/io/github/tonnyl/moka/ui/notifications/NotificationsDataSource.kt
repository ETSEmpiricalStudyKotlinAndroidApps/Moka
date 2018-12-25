package io.github.tonnyl.moka.ui.notifications

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.net.NetworkState
import io.github.tonnyl.moka.net.service.NotificationsService
import io.github.tonnyl.moka.util.PageLinks
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.Executor

class NotificationsDataSource(
        private val notificationsService: NotificationsService,
        private val retryExecutor: Executor
) : PageKeyedDataSource<String, Notification>() {

    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter.
     */
    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Notification>) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        try {
            // triggered by a refresh, we better execute sync
            val response = notificationsService.listNotifications(true, 1, params.requestedLoadSize)
                    .execute()

            retry = null
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)

            val pl = PageLinks(response)
            callback.onResult(response.body() ?: emptyList(), pl.prev, pl.next)
        } catch (ioe: IOException) {
            Timber.e(ioe, "loadInitial params: $params error: ${ioe.message}")

            retry = {
                loadInitial(params, callback)
            }

            val error = NetworkState.error(ioe.message ?: "unknown error")
            networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Notification>) {
        networkState.postValue(NetworkState.LOADING)

        notificationsService.listNotificationsByUrl(params.key)
                .enqueue(object : Callback<List<Notification>> {

                    override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                        Timber.e(t, "loadAfter params: ${params.key} error: ${t.message}")

                        retry = {
                            loadAfter(params, callback)
                        }

                        networkState.postValue(NetworkState.error(t.message ?: "unknown err"))
                    }

                    override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                        if (response.isSuccessful) {
                            retry = null

                            val pl = PageLinks(response)
                            Timber.d("pre: ${pl.prev} next: ${pl.next}")

                            callback.onResult((response.body()
                                    ?: emptyList()).toMutableList(), pl.next)

                            networkState.postValue(NetworkState.LOADED)
                        } else {
                            retry = {
                                loadAfter(params, callback)
                            }

                            networkState.postValue(NetworkState.error("error code: ${response.code()}"))
                        }
                    }

                })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Notification>) {
        networkState.postValue(NetworkState.LOADING)

        notificationsService.listNotificationsByUrl(params.key)
                .enqueue(object : Callback<List<Notification>> {

                    override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                        Timber.e(t, "loadBefore params: ${params.key} error: ${t.message}")

                        retry = {
                            loadBefore(params, callback)
                        }

                        networkState.postValue(NetworkState.error(t.message ?: "unknown err"))
                    }

                    override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                        if (response.isSuccessful) {
                            retry = null

                            val pl = PageLinks(response)
                            Timber.d("pre: ${pl.prev} next: ${pl.next}")

                            callback.onResult((response.body()
                                    ?: emptyList()).toMutableList(), pl.prev)

                            networkState.postValue(NetworkState.LOADED)
                        } else {
                            retry = {
                                loadBefore(params, callback)
                            }

                            networkState.postValue(NetworkState.error("error code: ${response.code()}"))
                        }
                    }

                })
    }

}