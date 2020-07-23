package io.github.tonnyl.moka.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.UserItem
import io.github.tonnyl.moka.databinding.ItemUserBinding

class UserAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: UsersViewModel
) : PagingDataAdapter<UserItem, UserAdapter.UserViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
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

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position) ?: return

        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_user

    class UserViewHolder(
        private val owner: LifecycleOwner,
        private val model: UsersViewModel,
        private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: UserItem) {
            with(binding) {
                lifecycleOwner = owner
                viewModel = model
                user = data

                executePendingBindings()
            }
        }

    }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserItem>() {

            override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem == newItem
            }

        }

    }

}