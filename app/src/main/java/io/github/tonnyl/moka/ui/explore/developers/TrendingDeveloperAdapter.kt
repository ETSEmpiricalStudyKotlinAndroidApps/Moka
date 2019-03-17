package io.github.tonnyl.moka.ui.explore.developers

import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.databinding.ItemTrendingDeveloperBinding

class TrendingDeveloperAdapter(
        var language: String,
        var since: String
) : ListAdapter<TrendingDeveloper, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrendingDeveloper>() {

            override fun areItemsTheSame(oldItem: TrendingDeveloper, newItem: TrendingDeveloper): Boolean = oldItem.url == newItem.url

            override fun areContentsTheSame(oldItem: TrendingDeveloper, newItem: TrendingDeveloper): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = TrendingDeveloperViewHolder(ItemTrendingDeveloperBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position) ?: return

        if (holder is TrendingDeveloperViewHolder) {
            holder.bindTo(data, position)
        }
    }

    class TrendingDeveloperViewHolder(
            private val binding: ItemTrendingDeveloperBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val foregroundColorSpan = ForegroundColorSpan(ResourcesCompat.getColor(binding.root.resources, R.color.colorTextPrimary, null))

        fun bindTo(data: TrendingDeveloper, position: Int) {
            binding.apply {
                rank = position
                span = foregroundColorSpan
                avatar = data.avatar
                login = data.username
                username = data.name
                repositoryName = data.repository.name
                repositoryDescription = data.repository.description
            }

            binding.executePendingBindings()
        }

    }

}