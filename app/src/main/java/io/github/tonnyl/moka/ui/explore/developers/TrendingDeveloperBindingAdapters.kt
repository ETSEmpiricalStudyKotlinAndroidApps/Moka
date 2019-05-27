package io.github.tonnyl.moka.ui.explore.developers

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import io.github.tonnyl.moka.R

@BindingAdapter("trendingRank")
fun AppCompatTextView.trendingRank(
        rank: Int
) {
    setTextFuture(PrecomputedTextCompat.getTextFuture(
            rank.toString(),
            TextViewCompat.getTextMetricsParams(this),
            null
    ))
}

@BindingAdapter(
        value = ["foregroundColorSpan", "trendingDeveloperLogin", "trendingDeveloperName"],
        requireAll = true
)
fun AppCompatTextView.trendingDeveloperName(
        foregroundColorSpan: ForegroundColorSpan,
        login: String,
        name: String?
) {
    setTextFuture(PrecomputedTextCompat.getTextFuture(
            name?.let {
                val nameSpannable = SpannableStringBuilder(context.getString(R.string.explore_trending_developer_name, login, it))
                nameSpannable.setSpan(foregroundColorSpan, nameSpannable.length - it.length - 2, nameSpannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                nameSpannable
            } ?: login,
            TextViewCompat.getTextMetricsParams(this),
            null
    ))
}

@BindingAdapter(
        value = ["foregroundColorSpan", "trendingDeveloperRepositoryName", "trendingDeveloperRepositoryDescription"],
        requireAll = true
)
fun AppCompatTextView.trendingDeveloperDescription(
        foregroundColorSpan: ForegroundColorSpan,
        repositoryName: String,
        repositoryDescription: String?
) {
    setTextFuture(PrecomputedTextCompat.getTextFuture(
            repositoryDescription?.let {
                val descriptionSpannable = SpannableStringBuilder(context.getString(R.string.explore_trending_developer_repository_name_description, repositoryName, it))
                descriptionSpannable.setSpan(foregroundColorSpan, 0, repositoryName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                descriptionSpannable
            } ?: repositoryName,
            TextViewCompat.getTextMetricsParams(this),
            null
    ))
}