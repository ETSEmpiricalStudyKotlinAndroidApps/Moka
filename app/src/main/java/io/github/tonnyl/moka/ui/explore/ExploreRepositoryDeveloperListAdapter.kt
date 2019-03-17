package io.github.tonnyl.moka.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.databinding.ItemTrendingRepositoryDeveloperItemBinding

class ExploreRepositoryDeveloperListAdapter : ListAdapter<TrendingDeveloper, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrendingDeveloper>() {

            override fun areItemsTheSame(oldItem: TrendingDeveloper, newItem: TrendingDeveloper): Boolean = oldItem.url == newItem.url

            override fun areContentsTheSame(oldItem: TrendingDeveloper, newItem: TrendingDeveloper): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = TrendingRepositoryDeveloperViewHolder(ItemTrendingRepositoryDeveloperItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position) ?: return
        if (holder is TrendingRepositoryDeveloperViewHolder) {
            holder.bindTo(data)
        }
    }

    class TrendingRepositoryDeveloperViewHolder(
            private val binding: ItemTrendingRepositoryDeveloperItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: TrendingDeveloper) {
            binding.apply {
                avatar = data.avatar
                username = data.username
            }

            binding.executePendingBindings()
        }

    }

}