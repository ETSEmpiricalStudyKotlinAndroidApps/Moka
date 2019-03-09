package io.github.tonnyl.moka.ui.explore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.net.GlideLoader
import kotlinx.android.synthetic.main.item_trending_repository_developer_item.view.*

class ExploreRepositoryDeveloperListAdapter : ListAdapter<TrendingDeveloper, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrendingDeveloper>() {

            override fun areItemsTheSame(oldItem: TrendingDeveloper, newItem: TrendingDeveloper): Boolean = oldItem.url == newItem.url

            override fun areContentsTheSame(oldItem: TrendingDeveloper, newItem: TrendingDeveloper): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = TrendingRepositoryDeveloperViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trending_repository_developer_item, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position) ?: return
        if (holder is TrendingRepositoryDeveloperViewHolder) {
            holder.bindTo(data)
        }
    }

    class TrendingRepositoryDeveloperViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindTo(data: TrendingDeveloper) {
            with(itemView) {
                GlideLoader.loadAvatar(data.avatar, item_trending_repository_developer_avatar)

                item_trending_repository_developer_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                        data.username,
                        TextViewCompat.getTextMetricsParams(item_trending_repository_developer_name),
                        null
                ))
            }
        }

    }

}