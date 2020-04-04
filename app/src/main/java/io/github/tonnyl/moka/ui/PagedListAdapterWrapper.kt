package io.github.tonnyl.moka.ui

import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Status

class PagedListAdapterWrapper<T, VH : RecyclerView.ViewHolder>(
    val headerAdapter: LoadStateAdapter,
    val pagingAdapter: PagedListAdapter<T, VH>,
    val footerAdapter: LoadStateAdapter
) {

    val mergeAdapter by lazy(LazyThreadSafetyMode.NONE) {
        MergeAdapter(
            MergeAdapter.Config.Builder()
                .setIsolateViewTypes(false)
                .build(),
            headerAdapter,
            pagingAdapter,
            footerAdapter
        )
    }

    val observer by lazy(LazyThreadSafetyMode.NONE) {
        Observer<PagedResource<List<T>>> {
            when (it.resource?.status) {
                Status.SUCCESS -> {
                    headerAdapter.loadState = NetworkState.LOADED
                    footerAdapter.loadState = NetworkState.LOADED
                }
                Status.ERROR -> {
                    NetworkState.error(it.resource.message).let { error ->
                        when (it.direction) {
                            PagedResourceDirection.BEFORE -> {
                                headerAdapter.loadState = error
                            }
                            PagedResourceDirection.AFTER -> {
                                footerAdapter.loadState = error
                            }
                        }
                    }
                }
                Status.LOADING -> {
                    when (it.direction) {
                        PagedResourceDirection.BEFORE -> {
                            headerAdapter.loadState = NetworkState.LOADING
                        }
                        PagedResourceDirection.AFTER -> {
                            footerAdapter.loadState = NetworkState.LOADING
                        }
                    }
                }
                null -> {

                }
            }
        }
    }

}