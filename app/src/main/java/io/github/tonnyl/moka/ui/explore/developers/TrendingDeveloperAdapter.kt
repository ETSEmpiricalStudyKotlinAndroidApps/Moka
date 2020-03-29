package io.github.tonnyl.moka.ui.explore.developers

import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.databinding.ItemTrendingDeveloperBinding
import io.github.tonnyl.moka.ui.explore.ExploreViewModel

class TrendingDeveloperAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: ExploreViewModel
) : ListAdapter<TrendingDeveloper, RecyclerView.ViewHolder>(
    DIFF_CALLBACK
) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrendingDeveloper>() {

            override fun areItemsTheSame(
                oldItem: TrendingDeveloper,
                newItem: TrendingDeveloper
            ): Boolean = oldItem.url == newItem.url

            override fun areContentsTheSame(
                oldItem: TrendingDeveloper,
                newItem: TrendingDeveloper
            ): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TrendingDeveloperViewHolder(
            lifecycleOwner,
            viewModel,
            ItemTrendingDeveloperBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position) ?: return

        if (holder is TrendingDeveloperViewHolder) {
            holder.bindTo(data, position)
        }
    }

    class TrendingDeveloperViewHolder(
        private val owner: LifecycleOwner,
        private val model: ExploreViewModel,
        private val binding: ItemTrendingDeveloperBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val foregroundColorSpan = ForegroundColorSpan(
            ResourcesCompat.getColor(
                binding.root.resources,
                R.color.colorPrimary,
                null
            )
        )

        fun bindTo(
            data: TrendingDeveloper,
            position: Int
        ) {
            with(binding) {
                lifecycleOwner = owner
                rank = position + 1
                span = foregroundColorSpan
                developer = data
                viewModel = model

                executePendingBindings()
            }
        }

    }

}