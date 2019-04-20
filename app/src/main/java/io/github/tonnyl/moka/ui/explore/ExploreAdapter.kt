package io.github.tonnyl.moka.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.databinding.ItemTrendingInfoBinding
import io.github.tonnyl.moka.databinding.ItemTrendingRepositoryBinding
import io.github.tonnyl.moka.databinding.ItemTrendingRepositoryDevelopersListBinding

class ExploreAdapter(
        var language: String,
        var since: String,
        var repositories: List<TrendingRepository>,
        var developers: List<TrendingDeveloper>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var actions: ExploreRepositoryActions? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.item_trending_info -> TrendingInfoViewHolder(ItemTrendingInfoBinding.inflate(inflater, parent, false))
            R.layout.item_trending_repository_developers_list -> TrendingRepositoryDeveloperListViewHolder(ItemTrendingRepositoryDevelopersListBinding.inflate(inflater, parent, false))
            else -> TrendingRepositoryViewHolder(ItemTrendingRepositoryBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_trending_info -> {
                if (holder is TrendingInfoViewHolder) {
                    holder.bindTo(language, since)
                }
            }
            R.layout.item_trending_repository_developers_list -> {
                if (holder is TrendingRepositoryDeveloperListViewHolder) {
                    holder.bindTo(developers)
                }
            }
            R.layout.item_trending_repository -> {
                if (holder is TrendingRepositoryViewHolder) {
                    holder.bindTo(repositories[position - 2], position - 2, actions)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> R.layout.item_trending_info
        1 -> R.layout.item_trending_repository_developers_list
        else -> R.layout.item_trending_repository
    }

    override fun getItemCount(): Int = repositories.size + 2

    class TrendingRepositoryDeveloperListViewHolder(
            private val binding: ItemTrendingRepositoryDevelopersListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: List<TrendingDeveloper>) {
            val adapter = ExploreRepositoryDeveloperListAdapter()

            with(binding.itemTrendingRepositoryDevelopersList) {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                setHasFixedSize(true)
                this.adapter = adapter
            }

            adapter.submitList(data)

            binding.executePendingBindings()
        }

    }

    class TrendingRepositoryViewHolder(
            private val binding: ItemTrendingRepositoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(
                data: TrendingRepository,
                position: Int,
                repositoryActions: ExploreRepositoryActions?
        ) {
            binding.apply {
                repository = data
                this.position = position + 1
                this.period = "today"
                actions = repositoryActions
            }
        }

    }

    class TrendingInfoViewHolder(
            private val binding: ItemTrendingInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(language: String, since: String) {
            binding.apply {
                this.language = language
                this.since = since
            }

            binding.executePendingBindings()
        }

    }

}