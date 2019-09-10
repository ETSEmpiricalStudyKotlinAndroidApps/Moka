package io.github.tonnyl.moka.ui.account

import android.accounts.Account
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.databinding.ItemAccountBinding

class AccountAdapter(
    private val viewModel: AccountViewModel,
    private val lifecycleOwner: LifecycleOwner
) : ListAdapter<Triple<Account, String, AuthenticatedUser>, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Triple<Account, String, AuthenticatedUser>>() {

                override fun areItemsTheSame(
                    oldItem: Triple<Account, String, AuthenticatedUser>,
                    newItem: Triple<Account, String, AuthenticatedUser>
                ): Boolean {
                    return oldItem.third.id == newItem.third.id
                }

                override fun areContentsTheSame(
                    oldItem: Triple<Account, String, AuthenticatedUser>,
                    newItem: Triple<Account, String, AuthenticatedUser>
                ): Boolean {
                    return oldItem == newItem
                }

            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AccountViewHolder(
            ItemAccountBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AccountViewHolder).bindTo(
            getItem(position).third,
            viewModel,
            lifecycleOwner
        )
    }

    class AccountViewHolder(
        private val binding: ItemAccountBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(
            user: AuthenticatedUser,
            viewModel: AccountViewModel,
            lifecycleOwner: LifecycleOwner
        ) {
            binding.run {
                this.viewModel = viewModel
                this.lifecycleOwner = lifecycleOwner
                account = user
                executePendingBindings()
            }
        }

    }

}