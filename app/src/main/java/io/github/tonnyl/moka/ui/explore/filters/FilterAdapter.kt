package io.github.tonnyl.moka.ui.explore.filters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.explore.TrendingTimeSpanType
import kotlinx.android.synthetic.main.item_trending_filter_time_span.view.*
import kotlinx.android.synthetic.main.item_trending_language.view.*

class FilterAdapter : ListAdapter<LocalLanguage?, RecyclerView.ViewHolder>(DIFF_CALLBACK),
        View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    /**
     * View -> Clicked [View]
     * First String -> [LocalLanguage.urlParam].
     * Second String -> [LocalLanguage.name].
     */
    var onLanguageItemClickListener: (View, String, String) -> Unit = { _, _, _ ->

    }

    var onRadioButtonClickListener: (View, TrendingTimeSpanType) -> Unit = { _, _ ->

    }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LocalLanguage?>() {

            override fun areItemsTheSame(oldItem: LocalLanguage, newItem: LocalLanguage): Boolean = oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: LocalLanguage, newItem: LocalLanguage): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_trending_filter_time_span -> TimeSpanViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trending_filter_time_span, parent, false))
        R.layout.item_trending_filter_language_label -> LanguageLabelViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trending_filter_language_label, parent, false))
        else -> LanguageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trending_language, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TimeSpanViewHolder -> {
                with(holder.itemView) {
                    item_trending_filter_time_span_radio_group.setOnCheckedChangeListener(this@FilterAdapter)
                }
            }
            is LanguageLabelViewHolder -> {

            }
            is LanguageViewHolder -> {
                getItem(position)?.let { localLanguage ->
                    holder.bindTo(localLanguage)
                    with(holder.itemView) {
                        tag = Pair(localLanguage.urlParam, localLanguage.name)
                        setOnClickListener(this@FilterAdapter)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> R.layout.item_trending_filter_time_span
        1 -> R.layout.item_trending_filter_language_label
        else -> R.layout.item_trending_language
    }

    override fun getItem(position: Int): LocalLanguage? = when (position) {
        0, 1 -> null
        else -> super.getItem(position - 2)
    }

    override fun getItemCount(): Int = super.getItemCount() + 2

    override fun onClick(v: View?) {
        v ?: return
        val tag = v.tag
        if (tag is Pair<*, *> && tag.first is String && tag.second is String) {
            onLanguageItemClickListener.invoke(v, tag.first as String, tag.second as String)
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        group ?: return

        val timeSpan = when (checkedId) {
            R.id.item_trending_filter_time_span_weekly -> {
                TrendingTimeSpanType.WEEKLY
            }
            R.id.item_trending_filter_time_span_monthly -> {
                TrendingTimeSpanType.MONTHLY
            }
            // including R.id.item_trending_filter_time_span_daily
            else -> {
                TrendingTimeSpanType.DAILY
            }
        }

        onRadioButtonClickListener.invoke(group.findViewById(checkedId), timeSpan)
    }

    class LanguageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindTo(language: LocalLanguage) {
            with(itemView) {
                item_language_text.text = language.name
                (item_language_text.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(Color.parseColor(language.color))
            }
        }

    }

    class TimeSpanViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class LanguageLabelViewHolder(view: View) : RecyclerView.ViewHolder(view)

}