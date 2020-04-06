package io.github.tonnyl.moka.ui

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import timber.log.Timber

typealias InitialLoadResponse<T> = Triple<List<T>, PreviousPageKey, NextPageKey>

typealias AfterLoadResponse<T> = Pair<List<T>, NextPageKey>

typealias BeforeLoadResponse<T> = Pair<List<T>, PreviousPageKey>

inline class PreviousPageKey(val value: String?)

inline class NextPageKey(val value: String?)

abstract class PageKeyedDataSourceWithLoadState<T>(
    var retry: (() -> Any)? = null
) : PageKeyedDataSource<String, T>() {

    abstract val initial: MutableLiveData<Resource<List<T>>>
    abstract val previousOrNext: MutableLiveData<PagedResource<List<T>>>

    abstract fun doLoadInitial(params: LoadInitialParams<String>): InitialLoadResponse<T>

    abstract fun doLoadAfter(params: LoadParams<String>): AfterLoadResponse<T>

    abstract fun doLoadBefore(params: LoadParams<String>): BeforeLoadResponse<T>

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, T>
    ) {
        initial.postValue(Resource.loading(null))

        try {
            val (data, previousKey, nextKey) = doLoadInitial(params)

            initial.postValue(Resource.success(data))

            callback.onResult(data, previousKey.value, nextKey.value)
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadInitial(params, callback)
            }

            initial.postValue(Resource.error(e.message, null))
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, T>) {
        previousOrNext.postValue(
            PagedResource(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val (data, nextPageKey) = doLoadAfter(params)

            retry = null

            previousOrNext.postValue(
                PagedResource(PagedResourceDirection.AFTER, Resource.success(data))
            )

            callback.onResult(data, nextPageKey.value)
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadAfter(params, callback)
            }

            previousOrNext.postValue(
                PagedResource(
                    PagedResourceDirection.AFTER,
                    Resource.error(e.message, null)
                )
            )
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, T>) {
        previousOrNext.postValue(
            PagedResource(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val (data, previousPageKey) = doLoadBefore(params)

            retry = null

            previousOrNext.postValue(
                PagedResource(PagedResourceDirection.BEFORE, Resource.success(data))
            )

            callback.onResult(data, previousPageKey.value)
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadAfter(params, callback)
            }

            previousOrNext.postValue(
                PagedResource(PagedResourceDirection.BEFORE, Resource.error(e.message, null))
            )
        }
    }

}