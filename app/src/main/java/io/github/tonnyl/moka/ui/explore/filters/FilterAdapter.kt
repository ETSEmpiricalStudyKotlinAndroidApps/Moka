package io.github.tonnyl.moka.ui.explore.filters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.ItemTrendingFilterTimeSpanBinding
import io.github.tonnyl.moka.databinding.ItemTrendingLanguageBinding
import io.github.tonnyl.moka.ui.explore.ExploreTimeSpanType

class FilterAdapter : ListAdapter<LocalLanguage?, RecyclerView.ViewHolder>(DIFF_CALLBACK),
        View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    /**
     * View -> Clicked [View]
     * First String -> [LocalLanguage.urlParam].
     * Second String -> [LocalLanguage.name].
     */
    var onLanguageItemClickListener: (View, String, String) -> Unit = { _, _, _ ->

    }

    var onRadioButtonClickListener: (View, ExploreTimeSpanType) -> Unit = { _, _ ->

    }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LocalLanguage?>() {

            override fun areItemsTheSame(oldItem: LocalLanguage, newItem: LocalLanguage): Boolean = oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: LocalLanguage, newItem: LocalLanguage): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.item_trending_filter_time_span -> TimeSpanViewHolder(ItemTrendingFilterTimeSpanBinding.inflate(inflater, parent, false))
            R.layout.item_trending_filter_language_label -> LanguageLabelViewHolder(inflater.inflate(R.layout.item_trending_filter_language_label, parent, false))
            else -> LanguageViewHolder(ItemTrendingLanguageBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TimeSpanViewHolder -> {
                holder.bindTo(ExploreTimeSpanType.DAILY)
            }
            is LanguageLabelViewHolder -> {

            }
            is LanguageViewHolder -> {
                val item = getItem(position) ?: return

                holder.bindTo(item)
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
                ExploreTimeSpanType.WEEKLY
            }
            R.id.item_trending_filter_time_span_monthly -> {
                ExploreTimeSpanType.MONTHLY
            }
            // including R.id.item_trending_filter_time_span_daily
            else -> {
                ExploreTimeSpanType.DAILY
            }
        }

        onRadioButtonClickListener.invoke(group.findViewById(checkedId), timeSpan)
    }

    class LanguageViewHolder(
            private val binding: ItemTrendingLanguageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(language: LocalLanguage) {
            binding.apply {
                this.language = language
            }

            binding.executePendingBindings()
        }

    }

    class TimeSpanViewHolder(
            private val binding: ItemTrendingFilterTimeSpanBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(selected: ExploreTimeSpanType) {
            binding.apply {
                selectedType = selected
            }

            binding.executePendingBindings()
        }

    }

    class LanguageLabelViewHolder(view: View) : RecyclerView.ViewHolder(view)

}