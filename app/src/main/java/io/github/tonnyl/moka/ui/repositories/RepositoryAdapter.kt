package io.github.tonnyl.moka.ui.repositories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.RepositoryAbstract
import io.github.tonnyl.moka.databinding.ItemRepositoryBinding

class RepositoryAdapter : PagedListAdapter<RepositoryAbstract, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    var repositoryActions: ItemRepositoryActions? = null

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RepositoryAbstract>() {

            override fun areItemsTheSame(oldItem: RepositoryAbstract, newItem: RepositoryAbstract): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: RepositoryAbstract, newItem: RepositoryAbstract): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = RepositoryViewHolder(ItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (holder is RepositoryViewHolder) {
            holder.bindTo(item, repositoryActions)
        }
    }

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