package io.github.tonnyl.moka.ui.search.repositories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem
import io.github.tonnyl.moka.databinding.ItemNetworkStateBinding
import io.github.tonnyl.moka.databinding.ItemSearchedRepositoryBinding
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.ui.NetworkStateViewHolder
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class SearchedRepositoryAdapter(
    private val retryActions: PagingNetworkStateActions
) : PagedListAdapter<SearchedRepositoryItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private var beforeNetworkState: NetworkState? = null
    private var afterNetworkState: NetworkState? = null

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchedRepositoryItem>() {

            override fun areItemsTheSame(
                oldItem: SearchedRepositoryItem,
                newItem: SearchedRepositoryItem
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: SearchedRepositoryItem,
                newItem: SearchedRepositoryItem
            ): Boolean = oldItem.id == newItem.id

        }

        const val VIEW_TYPE_BEFORE_NETWORK_STATE = 0x00
        const val VIEW_TYPE_REPOSITORY = 0x01
        const val VIEW_TYPE_AFTER_NETWORK_STATE = 0x02

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_BEFORE_NETWORK_STATE -> {
                NetworkStateViewHolder(
                    ItemNetworkStateBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_AFTER_NETWORK_STATE -> {
                NetworkStateViewHolder(
                    ItemNetworkStateBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_REPOSITORY -> RepositoryViewHolder(
                ItemSearchedRepositoryBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            else -> {
                throw IllegalArgumentException("Invalid view type: $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_REPOSITORY -> {
                val item = getItem(position) ?: return

                (holder as RepositoryViewHolder).bindTo(item)
            }
            VIEW_TYPE_AFTER_NETWORK_STATE -> {
                (holder as NetworkStateViewHolder).bindTo(afterNetworkState, retryActions)
            }
            VIEW_TYPE_BEFORE_NETWORK_STATE -> {
                (holder as NetworkStateViewHolder).bindTo(beforeNetworkState, retryActions)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when {
        hasBeforeExtraRow() && position == 0 -> VIEW_TYPE_BEFORE_NETWORK_STATE
        hasAfterExtraRow() && position == itemCount - 1 -> VIEW_TYPE_AFTER_NETWORK_STATE
        else -> VIEW_TYPE_REPOSITORY
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasBeforeExtraRow()) 1 else 0 + if (hasAfterExtraRow()) 1 else 0
    }

    private fun hasBeforeExtraRow() =
        beforeNetworkState != null && beforeNetworkState != NetworkState.LOADED

    private fun hasAfterExtraRow() =
        afterNetworkState != null && afterNetworkState != NetworkState.LOADED

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

}