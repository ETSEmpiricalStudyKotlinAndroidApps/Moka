package io.github.tonnyl.moka.ui.explore.repositories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.databinding.ItemTrendingRepositoryBinding
import io.github.tonnyl.moka.ui.explore.ExploreRepositoryActions

class TrendingRepositoryAdapter(
        var language: String,
        var since: String
) : ListAdapter<TrendingRepository, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    var actions: ExploreRepositoryActions? = null

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrendingRepository>() {

            override fun areItemsTheSame(oldItem: TrendingRepository, newItem: TrendingRepository): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: TrendingRepository, newItem: TrendingRepository): Boolean {
                return oldItem == newItem
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TrendingRepositoryViewHolder(ItemTrendingRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TrendingRepositoryViewHolder) {
            holder.bindTo(language, since, getItem(position), position, actions)
        }
    }

    class TrendingRepositoryViewHolder(
            private val binding: ItemTrendingRepositoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(
                language: String,
                since: String,
                data: TrendingRepository,
                position: Int,
                repositoryActions: ExploreRepositoryActions?
        ) {
            binding.apply {
                this.language = language
                this.since = since
                this.repository = data
                this.position = position + 1
                this.period = "today"
                actions = repositoryActions
            }

            binding.executePendingBindings()
        }

    }

}