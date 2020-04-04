package io.github.tonnyl.moka.ui.search.repositories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem
import io.github.tonnyl.moka.databinding.ItemSearchedRepositoryBinding

class SearchedRepositoryAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: SearchedRepositoriesViewModel
) : PagedListAdapter<SearchedRepositoryItem, SearchedRepositoryAdapter.RepositoryViewHolder>(
    DIFF_CALLBACK
) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchedRepositoryItem>() {

            override fun areItemsTheSame(
                oldItem: SearchedRepositoryItem,
                newItem: SearchedRepositoryItem
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: SearchedRepositoryItem,
                newItem: SearchedRepositoryItem
            ): Boolean = oldItem.id == newItem.id

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        return RepositoryViewHolder(
            lifecycleOwner,
            viewModel,
            ItemSearchedRepositoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bindTo(item)
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_searched_repository

    class RepositoryViewHolder(
        private val owner: LifecycleOwner,
        private val model: SearchedRepositoriesViewModel,
        private val binding: ItemSearchedRepositoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(repo: SearchedRepositoryItem) {
            with(binding) {
                lifecycleOwner = owner
                data = repo
                viewModel = model

                executePendingBindings()
            }
        }

    }

}