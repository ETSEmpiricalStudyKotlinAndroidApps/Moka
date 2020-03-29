package io.github.tonnyl.moka.ui.search.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.SearchedOrganizationItem
import io.github.tonnyl.moka.data.item.SearchedUserItem
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.databinding.ItemSearchedOrganizationBinding
import io.github.tonnyl.moka.databinding.ItemSearchedUserBinding
import io.github.tonnyl.moka.ui.PagedResourceAdapter
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class SearchedUserAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: SearchedUsersViewModel,
    override val retryActions: PagingNetworkStateActions
) : PagedResourceAdapter<SearchedUserOrOrgItem>(DIFF_CALLBACK, retryActions) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchedUserOrOrgItem>() {

            override fun areItemsTheSame(
                oldItem: SearchedUserOrOrgItem,
                newItem: SearchedUserOrOrgItem
            ): Boolean = oldItem.areItemsTheSame(newItem)

            override fun areContentsTheSame(
                oldItem: SearchedUserOrOrgItem,
                newItem: SearchedUserOrOrgItem
            ): Boolean = oldItem.areContentsTheSame(newItem)

        }

    }

    override fun initiateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_searched_user -> {
                UserViewHolder(
                    lifecycleOwner,
                    viewModel,
                    ItemSearchedUserBinding.inflate(inflater, parent, false)
                )
            }
            R.layout.item_searched_organization -> {
                OrganizationViewHolder(
                    lifecycleOwner,
                    viewModel,
                    ItemSearchedOrganizationBinding.inflate(inflater, parent, false)
                )
            }
            else -> {
                throw  IllegalArgumentException("Invalid view type: $viewType")
            }
        }
    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        when (getViewType(position)) {
            R.layout.item_searched_user -> {
                (holder as UserViewHolder).bindTo(item as SearchedUserItem)
            }
            R.layout.item_searched_organization -> {
                (holder as OrganizationViewHolder).bindTo(item as SearchedOrganizationItem)
            }
        }
    }

    override fun getViewType(position: Int): Int {
        return when (getItem(position)) {
            is SearchedUserItem -> {
                R.layout.item_searched_user
            }
            // is SearchedOrganizationItem
            else -> {
                R.layout.item_searched_organization
            }
        }
    }

    class UserViewHolder(
        private val owner: LifecycleOwner,
        private val model: SearchedUsersViewModel,
        private val binding: ItemSearchedUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(user: SearchedUserItem) {
            with(binding) {
                lifecycleOwner = owner
                data = user
                viewModel = model

                executePendingBindings()
            }
        }

    }

    class OrganizationViewHolder(
        private val owner: LifecycleOwner,
        private val model: SearchedUsersViewModel,
        private val binding: ItemSearchedOrganizationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(org: SearchedOrganizationItem) {
            with(binding) {
                lifecycleOwner = owner
                data = org
                viewModel = model

                executePendingBindings()
            }
        }

    }

}