package io.github.tonnyl.moka.ui.search.page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem
import io.github.tonnyl.moka.databinding.ItemSearchedRepositoryBinding

class SearchedRepositoryAdapter : PagedListAdapter<SearchedRepositoryItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchedRepositoryItem>() {

            override fun areItemsTheSame(oldItem: SearchedRepositoryItem, newItem: SearchedRepositoryItem): Boolean = oldItem == newItem

            override fun areContentsTheSame(oldItem: SearchedRepositoryItem, newItem: SearchedRepositoryItem): Boolean = oldItem.id == newItem.id

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = RepositoryViewHolder(ItemSearchedRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (holder is RepositoryViewHolder) {
            holder.bindTo(item)
        }
    }

    class RepositoryViewHolder(
            private val binding: ItemSearchedRepositoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: SearchedRepositoryItem) {
            binding.data = data
            binding.executePendingBindings()
        }

    }

}