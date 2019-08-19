package io.github.tonnyl.moka.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.databinding.ItemNetworkStateBinding
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.PagedResourceDirection

abstract class PagedResourceAdapter<T>(
    open val diffCallback: DiffUtil.ItemCallback<T>,
    open val retryActions: PagingNetworkStateActions
) : PagedListAdapter<T, RecyclerView.ViewHolder>(
    diffCallback
) {

    var networkState: Pair<PagedResourceDirection, NetworkState>? = null
        private set

    companion object {

        const val VIEW_TYPE_BEFORE_NETWORK_STATE = Int.MIN_VALUE

    }

    abstract fun initiateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    abstract fun bindHolder(holder: RecyclerView.ViewHolder, position: Int)

    abstract fun getViewType(position: Int): Int

    @Deprecated(
        message = "Use initiateViewHolder() instead",
        replaceWith = ReplaceWith("initiateViewHolder()"),
        level = DeprecationLevel.ERROR
    )
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_BEFORE_NETWORK_STATE) {
            NetworkStateViewHolder(
                ItemNetworkStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            initiateViewHolder(parent, viewType)
        }
    }

    @Deprecated(
        message = "Use bindHolder() instead",
        replaceWith = ReplaceWith("bindHolder()"),
        level = DeprecationLevel.ERROR
    )
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NetworkStateViewHolder) {
            holder.bindTo(networkState?.second, retryActions)
        } else {
            if (hasExtraRow()
                && networkState?.first == PagedResourceDirection.BEFORE
            ) {
                bindHolder(holder, position - 1)
            } else {
                bindHolder(holder, position)
            }
        }
    }

    @Deprecated(
        message = "Use bindHolder() instead",
        replaceWith = ReplaceWith("bindHolder()"),
        level = DeprecationLevel.ERROR
    )
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }

    @Deprecated(
        message = "Use getViewType() instead",
        replaceWith = ReplaceWith("getViewType", "holder", "position"),
        level = DeprecationLevel.ERROR
    )
    override fun getItemViewType(position: Int): Int {
        return if (
            (position == 0
                    && hasExtraRow()
                    && networkState?.first == PagedResourceDirection.BEFORE)
            || (position == itemCount - 1
                    && hasExtraRow()
                    && networkState?.first == PagedResourceDirection.AFTER)
        ) {
            VIEW_TYPE_BEFORE_NETWORK_STATE
        } else {
            if (hasExtraRow()
                && networkState?.first == PagedResourceDirection.BEFORE
            ) {
                getViewType(position - 1)
            } else {
                getViewType(position)
            }
        }
    }

    override fun getItemCount(): Int = super.getItemCount() + if (hasExtraRow()) 1 else 0

    fun setNetworkState(newNetworkState: Pair<PagedResourceDirection, NetworkState>?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                if (previousState?.first == PagedResourceDirection.BEFORE) {
                    notifyItemRemoved(0)
                } else {
                    notifyItemRemoved(super.getItemCount())
                }
            } else {
                if (newNetworkState?.first == PagedResourceDirection.BEFORE) {
                    notifyItemInserted(0)
                } else {
                    notifyItemInserted(super.getItemCount())
                }
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            if (newNetworkState?.first == PagedResourceDirection.BEFORE) {
                notifyItemChanged(0)
            } else {
                notifyItemChanged(super.getItemCount())
            }
        }
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState?.second != NetworkState.LOADED
    }

}