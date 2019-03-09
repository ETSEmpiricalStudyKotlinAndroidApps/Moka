package io.github.tonnyl.moka.ui.explore.developers

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.net.GlideLoader
import kotlinx.android.synthetic.main.item_trending_developer.view.*

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = TrendingDeveloperViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trending_developer, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position) ?: return

        if (holder is TrendingDeveloperViewHolder) {
            holder.bindTo(data, position)
        }
    }

    class TrendingDeveloperViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val foregroundColorSpan = ForegroundColorSpan(ResourcesCompat.getColor(view.resources, R.color.colorTextPrimary, null))

        fun bindTo(data: TrendingDeveloper, position: Int) {
            with(itemView) {
                GlideLoader.loadAvatar(data.avatar, item_trending_developer_avatar)

                item_trending_developer_rank.setTextFuture(PrecomputedTextCompat.getTextFuture(
                        (position + 1).toString(),
                        TextViewCompat.getTextMetricsParams(item_trending_developer_rank),
                        null
                ))

                item_trending_developer_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                        data.name?.let {
                            val nameSpannable = SpannableStringBuilder(context.getString(R.string.explore_trending_developer_name, data.username, data.name))
                            nameSpannable.setSpan(foregroundColorSpan, nameSpannable.length - data.name.length - 2, nameSpannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                            nameSpannable
                        } ?: data.username,
                        TextViewCompat.getTextMetricsParams(item_trending_developer_name),
                        null
                ))

                item_trending_developer_name_description.setTextFuture(PrecomputedTextCompat.getTextFuture(
                        data.repository.description?.let {
                            val descriptionSpannable = SpannableStringBuilder(context.getString(R.string.explore_trending_developer_repository_name_description, data.repository.name, data.repository.description))
                            descriptionSpannable.setSpan(foregroundColorSpan, 0, data.repository.name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                            descriptionSpannable
                        } ?: data.repository.name,
                        TextViewCompat.getTextMetricsParams(item_trending_developer_name_description),
                        null
                ))
            }
        }

    }

}