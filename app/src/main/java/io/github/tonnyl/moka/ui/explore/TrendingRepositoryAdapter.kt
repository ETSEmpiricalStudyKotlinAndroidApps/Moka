package io.github.tonnyl.moka.ui.explore

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.net.GlideLoader
import io.github.tonnyl.moka.util.formatNumberWithSuffix
import kotlinx.android.synthetic.main.item_trending_repository.view.*

class TrendingRepositoryAdapter(
        var language: String,
        var since: String
) : ListAdapter<TrendingRepository?, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrendingRepository?>() {

            override fun areItemsTheSame(oldItem: TrendingRepository, newItem: TrendingRepository): Boolean = oldItem.url == newItem.url

            override fun areContentsTheSame(oldItem: TrendingRepository, newItem: TrendingRepository): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_trending_info -> TrendingInfoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trending_info, parent, false))
        else -> TrendingRepositoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trending_repository, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            R.layout.item_trending_info -> {
                if (holder is TrendingInfoViewHolder) {
                    holder.bindTo(language, since)
                }
            }
            R.layout.item_trending_repository -> {
                if (holder is TrendingRepositoryViewHolder) {
                    getItem(position)?.let {
                        holder.bindTo(it)
                    }
                }
            }
        }
    }

    override fun getItem(position: Int): TrendingRepository? = when (position) {
        0 -> null
        else -> super.getItem(position - 1)
    }

    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> R.layout.item_trending_info
        else -> R.layout.item_trending_repository
    }

    override fun getItemCount(): Int = super.getItemCount() + 1

    class TrendingRepositoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindTo(data: TrendingRepository) {
            with(itemView) {
                if (data.builtBy.isNotEmpty()) {
                    GlideLoader.loadAvatar(data.builtBy.firstOrNull()?.avatar, item_trending_repository_user_avatar)
                }

                item_trending_repository_title.text = context.getString(R.string.repository_name_with_username, data.author, data.name)
                item_trending_repository_rank.text = layoutPosition.toString()
                item_trending_repository_caption.text = data.description ?: ""
                item_trending_repository_period_stars.text = context.getString(R.string.explore_period_stars, data.currentPeriodStars, "today")
                item_trending_repository_language_text.text = if (!data.language.isNullOrEmpty()) data.language else context.getString(R.string.programming_language_unknown)
                item_trending_repository_star_count_text.text = formatNumberWithSuffix(data.stars)
                item_trending_repository_fork_count_text.text = formatNumberWithSuffix(data.forks)
                (item_trending_repository_language_text.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(if (!data.languageColor.isNullOrEmpty()) Color.parseColor(data.languageColor) else Color.BLACK)
            }
        }

    }

}