package io.github.tonnyl.moka.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.UserQuery
import io.github.tonnyl.moka.databinding.ItemProfileOrganizationSimpleBinding

class ProfileOrganizationAdapter : ListAdapter<UserQuery.Node, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserQuery.Node>() {

            override fun areItemsTheSame(oldItem: UserQuery.Node, newItem: UserQuery.Node): Boolean = oldItem.avatarUrl() == newItem.avatarUrl()

            override fun areContentsTheSame(oldItem: UserQuery.Node, newItem: UserQuery.Node): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = ProfileOrganizationViewHolder(ItemProfileOrganizationSimpleBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (holder is ProfileOrganizationViewHolder) {
            holder.bindTo(item)
        }
    }

    class ProfileOrganizationViewHolder(
            private val binding: ItemProfileOrganizationSimpleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: UserQuery.Node) {
            binding.apply {
                avatar = data.avatarUrl().toString()
            }

            binding.executePendingBindings()
        }

    }

}