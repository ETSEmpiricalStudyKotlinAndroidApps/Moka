package io.github.tonnyl.moka.ui.explore.repositories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.databinding.ItemTrendingRepositoryBinding

class TrendingRepositoryAdapter : ListAdapter<TrendingRepository, RecyclerView.ViewHolder>(
    DIFF_CALLBACK
) {

    var actions: TrendingRepositoryAction? = null

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrendingRepository>() {

            override fun areItemsTheSame(
                oldItem: TrendingRepository,
                newItem: TrendingRepository
            ): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(
                oldItem: TrendingRepository,
                newItem: TrendingRepository
            ): Boolean {
                return oldItem == newItem
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TrendingRepositoryViewHolder(
            ItemTrendingRepositoryBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TrendingRepositoryViewHolder) {
            holder.bindTo(getItem(position), position, actions)
        }
    }

    class TrendingRepositoryViewHolder(
        private val binding: ItemTrendingRepositoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(
            data: TrendingRepository,
            position: Int,
            repositoryActions: TrendingRepositoryAction?
        ) {
            binding.apply {
                repository = data
                rank = position + 1
                actions = repositoryActions
            }

            binding.executePendingBindings()
        }

    }

}