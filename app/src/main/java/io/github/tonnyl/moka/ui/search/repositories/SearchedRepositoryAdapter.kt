package io.github.tonnyl.moka.ui.search.repositories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem
import io.github.tonnyl.moka.databinding.ItemNetworkStateBinding
import io.github.tonnyl.moka.databinding.ItemSearchedRepositoryBinding
import io.github.tonnyl.moka.net.NetworkState

class SearchedRepositoryAdapter : PagedListAdapter<SearchedRepositoryItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private var beforeNetworkState: NetworkState? = null
    private var afterNetworkState: NetworkState? = null

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchedRepositoryItem>() {

            override fun areItemsTheSame(oldItem: SearchedRepositoryItem, newItem: SearchedRepositoryItem): Boolean = oldItem == newItem

            override fun areContentsTheSame(oldItem: SearchedRepositoryItem, newItem: SearchedRepositoryItem): Boolean = oldItem.id == newItem.id

        }

        const val VIEW_TYPE_BEFORE_NETWORK_STATE = 0x00
        const val VIEW_TYPE_REPOSITORY = 0x01
        const val VIEW_TYPE_AFTER_NETWORK_STATE = 0x02

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_REPOSITORY -> RepositoryViewHolder(ItemSearchedRepositoryBinding.inflate(inflater, parent, false))
            else -> NetworkStateViewHolder(ItemNetworkStateBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RepositoryViewHolder) {
            val item = getItem(position) ?: return

            holder.bindTo(item)
        } else if (holder is NetworkStateViewHolder) {
            val viewType = getItemViewType(position)
            if (viewType == VIEW_TYPE_BEFORE_NETWORK_STATE) {
                holder.bindTo(beforeNetworkState)
            } else {
                holder.bindTo(afterNetworkState)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when {
        hasBeforeExtraRow() && position == 0 -> VIEW_TYPE_BEFORE_NETWORK_STATE
        hasAfterExtraRow() && position == itemCount - 1 -> VIEW_TYPE_AFTER_NETWORK_STATE
        else -> VIEW_TYPE_REPOSITORY
    }

    override fun getItemCount(): Int = super.getItemCount() + if (hasBeforeExtraRow()) 1 else 0 + if (hasAfterExtraRow()) 1 else 0

    private fun hasBeforeExtraRow() = beforeNetworkState != null && beforeNetworkState != NetworkState.LOADED

    private fun hasAfterExtraRow() = afterNetworkState != null && afterNetworkState != NetworkState.LOADED

    fun setBeforeNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.beforeNetworkState
        val hadExtraRow = hasBeforeExtraRow()
        this.beforeNetworkState = newNetworkState
        val hasExtraRow = hasBeforeExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(0)
        }
    }

    fun setAfterNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.afterNetworkState
        val hadExtraRow = hasAfterExtraRow()
        this.afterNetworkState = newNetworkState
        val hasExtraRow = hasAfterExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    class RepositoryViewHolder(
            private val binding: ItemSearchedRepositoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: SearchedRepositoryItem) {
            binding.data = data
            binding.executePendingBindings()
        }

    }

    class NetworkStateViewHolder(
            private val binding: ItemNetworkStateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(networkState: NetworkState?) {
            binding.state = networkState
            binding.executePendingBindings()
        }

    }

}