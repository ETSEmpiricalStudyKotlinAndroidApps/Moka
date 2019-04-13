package io.github.tonnyl.moka.ui.search.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.item.SearchedOrganizationItem
import io.github.tonnyl.moka.data.item.SearchedUserItem
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.databinding.ItemNetworkStateBinding
import io.github.tonnyl.moka.databinding.ItemSearchedOrganizationBinding
import io.github.tonnyl.moka.databinding.ItemSearchedUserBinding
import io.github.tonnyl.moka.net.NetworkState

class SearchedUserAdapter : PagedListAdapter<SearchedUserOrOrgItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private var beforeNetworkState: NetworkState? = null
    private var afterNetworkState: NetworkState? = null

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchedUserOrOrgItem>() {

            override fun areItemsTheSame(oldItem: SearchedUserOrOrgItem, newItem: SearchedUserOrOrgItem): Boolean = oldItem.hashCode() == newItem.hashCode()

            override fun areContentsTheSame(oldItem: SearchedUserOrOrgItem, newItem: SearchedUserOrOrgItem): Boolean = oldItem.compare(newItem)

        }

        const val VIEW_TYPE_BEFORE_NETWORK_STATE = 0x00
        const val VIEW_TYPE_USER = 0x01
        const val VIEW_TYPE_ORGANIZATION = 0x02
        const val VIEW_TYPE_AFTER_NETWORK_STATE = 0x03

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_USER -> UserViewHolder(ItemSearchedUserBinding.inflate(inflater, parent, false))
            VIEW_TYPE_ORGANIZATION -> OrganizationViewHolder(ItemSearchedOrganizationBinding.inflate(inflater, parent, false))
            else -> NetworkStateViewHolder(ItemNetworkStateBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NetworkStateViewHolder) {
            val viewType = getItemViewType(position)
            if (viewType == VIEW_TYPE_BEFORE_NETWORK_STATE) {
                holder.bindTo(beforeNetworkState)
            } else {
                holder.bindTo(afterNetworkState)
            }
        } else {
            val item = getItem(position) ?: return

            if (getItemViewType(position) == VIEW_TYPE_USER) {
                (holder as UserViewHolder).bindTo(item as SearchedUserItem)
            } else {
                (holder as OrganizationViewHolder).bindTo(item as SearchedOrganizationItem)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when {
        hasBeforeExtraRow() && position == 0 -> VIEW_TYPE_BEFORE_NETWORK_STATE
        hasAfterExtraRow() && position == itemCount - 1 -> VIEW_TYPE_AFTER_NETWORK_STATE
        else -> {
            if (getItem(position) is SearchedUserItem) VIEW_TYPE_USER else VIEW_TYPE_ORGANIZATION
        }
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

    class UserViewHolder(
            private val binding: ItemSearchedUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: SearchedUserItem) {
            binding.data = data
            binding.executePendingBindings()
        }

    }

    class OrganizationViewHolder(
            private val binding: ItemSearchedOrganizationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: SearchedOrganizationItem) {
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