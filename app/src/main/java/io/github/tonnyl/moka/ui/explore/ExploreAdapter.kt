package io.github.tonnyl.moka.ui.explore

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.net.GlideLoader
import io.github.tonnyl.moka.util.formatNumberWithSuffix
import kotlinx.android.synthetic.main.item_trending_info.view.*
import kotlinx.android.synthetic.main.item_trending_repository.view.*
import kotlinx.android.synthetic.main.item_trending_repository_developers_list.view.*

class ExploreAdapter(
        var language: String,
        var since: String,
        var repositories: List<TrendingRepository>,
        var developers: List<TrendingDeveloper>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    var onItemClick: () -> Unit = {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_trending_info -> TrendingInfoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trending_info, parent, false))
        R.layout.item_trending_repository_developers_list -> TrendingRepositoryDeveloperListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trending_repository_developers_list, parent, false))
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
            R.layout.item_trending_repository_developers_list -> {
                if (holder is TrendingRepositoryDeveloperListViewHolder) {
                    holder.bindTo(developers)

                    holder.itemView.item_trending_repository_developers_all.setOnClickListener(this)
                }
            }
            R.layout.item_trending_repository -> {
                if (holder is TrendingRepositoryViewHolder) {
                    holder.bindTo(repositories[position - 2], position - 2)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.item_trending_repository_developers_all -> {
                onItemClick.invoke()
            }
        }
    }

    class TrendingRepositoryDeveloperListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindTo(data: List<TrendingDeveloper>) {
            with(itemView) {
                val adapter = ExploreRepositoryDeveloperListAdapter()

                item_trending_repository_developers_list.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                item_trending_repository_developers_list.setHasFixedSize(true)
                item_trending_repository_developers_list.adapter = adapter

                adapter.submitList(data)
            }
        }

    }

    class TrendingRepositoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindTo(data: TrendingRepository, position: Int) {
            with(itemView) {
                if (position == 0) {
                    item_trending_repository_overline.visibility = View.VISIBLE
                    item_trending_repository_space_between_overline_and_avatar.visibility = View.VISIBLE
                } else {
                    item_trending_repository_overline.visibility = View.GONE
                    item_trending_repository_space_between_overline_and_avatar.visibility = View.GONE
                }

                if (data.builtBy.isNotEmpty()) {
                    GlideLoader.loadAvatar(data.builtBy.firstOrNull()?.avatar, item_trending_repository_user_avatar)
                }

                item_trending_repository_title.text = context.getString(R.string.repository_name_with_username, data.author, data.name)
                item_trending_repository_rank.text = (position + 1).toString()
                item_trending_repository_caption.text = data.description ?: ""
                item_trending_repository_period_stars.text = context.getString(R.string.explore_period_stars, data.currentPeriodStars, "today")
                item_trending_repository_language_text.text = if (!data.language.isNullOrEmpty()) data.language else context.getString(R.string.programming_language_unknown)
                item_trending_repository_star_count_text.text = formatNumberWithSuffix(data.stars)
                item_trending_repository_fork_count_text.text = formatNumberWithSuffix(data.forks)
                (item_trending_repository_language_text.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(if (!data.languageColor.isNullOrEmpty()) Color.parseColor(data.languageColor) else Color.BLACK)
            }
        }

    }

    class TrendingInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindTo(language: String, since: String) {
            with(itemView) {
                item_trending_info_text.text = context.getString(R.string.explore_filter_info, language, since)
            }
        }

    }

}