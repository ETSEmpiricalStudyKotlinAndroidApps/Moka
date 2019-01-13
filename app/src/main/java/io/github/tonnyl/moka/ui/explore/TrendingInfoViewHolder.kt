package io.github.tonnyl.moka.ui.explore

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import kotlinx.android.synthetic.main.item_trending_info.view.*

class TrendingInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bindTo(language: String, since: String) {
        with(itemView) {
            item_trending_info_text.text = context.getString(R.string.explore_filter_info, language, since)
        }
    }

}