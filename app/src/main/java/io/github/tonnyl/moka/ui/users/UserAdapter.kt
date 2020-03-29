package io.github.tonnyl.moka.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.UserItem
import io.github.tonnyl.moka.databinding.ItemUserBinding
import io.github.tonnyl.moka.ui.PagedResourceAdapter
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class UserAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: UsersViewModel,
    override val retryActions: PagingNetworkStateActions
) : PagedResourceAdapter<UserItem>(DIFF_CALLBACK, retryActions) {

    companion object {

        const val VIEW_TYPE_USER = R.layout.item_user

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserItem>() {

            override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem == newItem
            }

        }

    }

    override fun initiateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserViewHolder(
            lifecycleOwner,
            viewModel,
            ItemUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (holder is UserViewHolder) {
            holder.bindTo(item)
        }
    }

    override fun getViewType(position: Int): Int = VIEW_TYPE_USER

    class UserViewHolder(
        private val owner: LifecycleOwner,
        private val model: UsersViewModel,
        private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: UserItem) {
            with(binding) {
                lifecycleOwner = owner
                viewModel = model
                user = data

                executePendingBindings()
            }
        }

    }

}