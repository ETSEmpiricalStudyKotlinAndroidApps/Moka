package io.github.tonnyl.moka.ui.explore.repositories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.databinding.ItemTrendingRepositoryBinding
import io.github.tonnyl.moka.ui.explore.ExploreViewModel

class TrendingRepositoryAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: ExploreViewModel
) : ListAdapter<TrendingRepository, RecyclerView.ViewHolder>(
    DIFF_CALLBACK
) {

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
            lifecycleOwner,
            viewModel,
            ItemTrendingRepositoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TrendingRepositoryViewHolder) {
            holder.bindTo(getItem(position), position)
        }
    }

    class TrendingRepositoryViewHolder(
        private val owner: LifecycleOwner,
        private val model: ExploreViewModel,
        private val binding: ItemTrendingRepositoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(
            data: TrendingRepository,
            position: Int
        ) {
            with(binding) {
                lifecycleOwner = owner
                viewModel = model
                repository = data
                rank = position + 1

                executePendingBindings()
            }
        }

    }

}