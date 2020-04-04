package io.github.tonnyl.moka.ui.repositories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.databinding.ItemRepositoryBinding

class RepositoryAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: RepositoriesViewModel
) : PagedListAdapter<RepositoryItem, RepositoryAdapter.RepositoryViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RepositoryItem>() {

            override fun areItemsTheSame(
                oldItem: RepositoryItem,
                newItem: RepositoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: RepositoryItem,
                newItem: RepositoryItem
            ): Boolean {
                return oldItem == newItem
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        return RepositoryViewHolder(
            lifecycleOwner,
            viewModel,
            ItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val item = getItem(position) ?: return

        holder.bindTo(item)
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_repository

    class RepositoryViewHolder(
        private val owner: LifecycleOwner,
        private val model: RepositoriesViewModel,
        private val binding: ItemRepositoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(repo: RepositoryItem) {
            with(binding) {
                lifecycleOwner = owner
                viewModel = model
                repository = repo

                executePendingBindings()
            }
        }

    }

}