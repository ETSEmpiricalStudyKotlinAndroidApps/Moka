package io.github.tonnyl.moka.ui.repositories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.RepositoryAbstract
import io.github.tonnyl.moka.databinding.ItemRepositoryBinding
import io.github.tonnyl.moka.ui.PagedResourceAdapter
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class RepositoryAdapter(
    override val retryActions: PagingNetworkStateActions
) : PagedResourceAdapter<RepositoryAbstract>(DIFF_CALLBACK, retryActions) {

    var repositoryActions: ItemRepositoryActions? = null

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RepositoryAbstract>() {

            override fun areItemsTheSame(
                oldItem: RepositoryAbstract,
                newItem: RepositoryAbstract
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: RepositoryAbstract,
                newItem: RepositoryAbstract
            ): Boolean {
                return oldItem == newItem
            }

        }

    }

    override fun initiateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RepositoryViewHolder(
            ItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (holder is RepositoryViewHolder) {
            holder.bindTo(item, repositoryActions)
        }
    }

    override fun getViewType(position: Int): Int = R.layout.item_repository

    class RepositoryViewHolder(
        private val binding: ItemRepositoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: RepositoryAbstract, repositoryActions: ItemRepositoryActions?) {
            binding.apply {
                repository = data
                actions = repositoryActions
            }

            binding.executePendingBindings()
        }

    }

}