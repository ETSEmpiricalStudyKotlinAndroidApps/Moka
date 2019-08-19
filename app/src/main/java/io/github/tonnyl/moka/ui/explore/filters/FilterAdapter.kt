package io.github.tonnyl.moka.ui.explore.filters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.ItemTrendingFilterTimeSpanBinding
import io.github.tonnyl.moka.databinding.ItemTrendingLanguageBinding
import io.github.tonnyl.moka.ui.explore.ExploreTimeSpanType
import java.util.*

class FilterAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    private var languages: List<LocalLanguage>? = Collections.emptyList()

    var filterActions: FilterActions? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.item_trending_filter_time_span -> TimeSpanViewHolder(
                ItemTrendingFilterTimeSpanBinding.inflate(inflater, parent, false)
            )
            R.layout.item_trending_filter_language_label -> LanguageLabelViewHolder(
                inflater.inflate(
                    R.layout.item_trending_filter_language_label,
                    parent,
                    false
                )
            )
            else -> LanguageViewHolder(ItemTrendingLanguageBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TimeSpanViewHolder -> {
                holder.bindTo(ExploreTimeSpanType.DAILY, filterActions)
            }
            is LanguageLabelViewHolder -> {

            }
            is LanguageViewHolder -> {
                val item = languages?.get(position - 2) ?: return

                holder.bindTo(item, filterActions)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> R.layout.item_trending_filter_time_span
        1 -> R.layout.item_trending_filter_language_label
        else -> R.layout.item_trending_language
    }

    override fun getItemCount(): Int = (languages?.size ?: 0) + 2

    override fun onClick(v: View?) {
        v ?: return
        val tag = v.tag
//        if (tag is Pair<*, *> && tag.first is String && tag.second is String) {
//            onLanguageItemClickListener.invoke(v, tag.first as String, tag.second as String)
//        }
    }

    fun updateDataSource(languages: List<LocalLanguage>?) {
        this.languages = languages
        notifyDataSetChanged()
    }

    class LanguageViewHolder(
        private val binding: ItemTrendingLanguageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(language: LocalLanguage, actions: FilterActions?) {
            binding.apply {
                this.language = language
                filterActions = actions
            }

            binding.executePendingBindings()
        }

    }

    class TimeSpanViewHolder(
        private val binding: ItemTrendingFilterTimeSpanBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(selected: ExploreTimeSpanType, actions: FilterActions?) {
            binding.apply {
                selectedType = selected
                filterActions = actions
            }

            binding.executePendingBindings()
        }

    }

    class LanguageLabelViewHolder(view: View) : RecyclerView.ViewHolder(view)

}