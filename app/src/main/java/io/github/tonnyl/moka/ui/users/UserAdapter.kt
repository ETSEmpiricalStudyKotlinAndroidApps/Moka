package io.github.tonnyl.moka.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.UserGraphQL
import io.github.tonnyl.moka.databinding.ItemUserBinding

class UserAdapter : PagedListAdapter<UserGraphQL, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    var actions: ItemUserActions? = null

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserGraphQL>() {

            override fun areItemsTheSame(oldItem: UserGraphQL, newItem: UserGraphQL): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UserGraphQL, newItem: UserGraphQL): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = UserViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (holder is UserViewHolder) {
            holder.bindTo(item, actions)
        }
    }

    class UserViewHolder(
            private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: UserGraphQL, userActions: ItemUserActions?) {
            binding.apply {
                avatar = data.avatarUrl.toString()
                username = data.name
                login = data.login
                bio = data.bio
                following = data.viewerIsFollowing
                actions = userActions
            }

            binding.executePendingBindings()
        }

    }

}