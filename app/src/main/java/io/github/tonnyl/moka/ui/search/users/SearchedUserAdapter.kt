package io.github.tonnyl.moka.ui.search.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.item.SearchedOrganizationItem
import io.github.tonnyl.moka.data.item.SearchedUserItem
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.databinding.ItemSearchedOrganizationBinding
import io.github.tonnyl.moka.databinding.ItemSearchedUserBinding

class SearchedUserAdapter : PagedListAdapter<SearchedUserOrOrgItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchedUserOrOrgItem>() {

            override fun areItemsTheSame(oldItem: SearchedUserOrOrgItem, newItem: SearchedUserOrOrgItem): Boolean = oldItem.hashCode() == newItem.hashCode()

            override fun areContentsTheSame(oldItem: SearchedUserOrOrgItem, newItem: SearchedUserOrOrgItem): Boolean = oldItem.compare(newItem)

        }

        const val VIEW_TYPE_USER = 0x00
        const val VIEW_TYPE_ORGANIZATION = 0x01

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == VIEW_TYPE_USER) {
            UserViewHolder(ItemSearchedUserBinding.inflate(inflater, parent, false))
        } else {
            OrganizationViewHolder(ItemSearchedOrganizationBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (getItemViewType(position) == VIEW_TYPE_USER) {
            (holder as UserViewHolder).bindTo(item as SearchedUserItem)
        } else {
            (holder as OrganizationViewHolder).bindTo(item as SearchedOrganizationItem)
        }
    }

    override fun getItemViewType(position: Int): Int = if (getItem(position) is SearchedUserItem) VIEW_TYPE_USER else VIEW_TYPE_ORGANIZATION

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

}