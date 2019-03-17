package io.github.tonnyl.moka.ui.explore.developers

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import io.github.tonnyl.moka.R

object TrendingDeveloperBindingAdapters {

    @JvmStatic
    @BindingAdapter("trendingRank")
    fun trendingRank(
            textView: AppCompatTextView,
            rank: Int
    ) {
        textView.setTextFuture(PrecomputedTextCompat.getTextFuture(
                (rank + 1).toString(),
                TextViewCompat.getTextMetricsParams(textView),
                null
        ))
    }

    @JvmStatic
    @BindingAdapter(
            value = ["foregroundColorSpan", "trendingDeveloperLogin", "trendingDeveloperName"],
            requireAll = true
    )
    fun trendingDeveloperName(
            textView: AppCompatTextView,
            foregroundColorSpan: ForegroundColorSpan,
            login: String,
            name: String?
    ) {
        textView.setTextFuture(PrecomputedTextCompat.getTextFuture(
                name?.let {
                    val nameSpannable = SpannableStringBuilder(textView.context.getString(R.string.explore_trending_developer_name, login, it))
                    nameSpannable.setSpan(foregroundColorSpan, nameSpannable.length - it.length - 2, nameSpannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    nameSpannable
                } ?: login,
                TextViewCompat.getTextMetricsParams(textView),
                null
        ))
    }

    @JvmStatic
    @BindingAdapter(
            value = ["foregroundColorSpan", "trendingDeveloperRepositoryName", "trendingDeveloperRepositoryDescription"],
            requireAll = true
    )
    fun trendingDeveloperDescription(
            textView: AppCompatTextView,
            foregroundColorSpan: ForegroundColorSpan,
            repositoryName: String,
            repositoryDescription: String?
    ) {
        textView.setTextFuture(PrecomputedTextCompat.getTextFuture(
                repositoryDescription?.let {
                    val descriptionSpannable = SpannableStringBuilder(textView.context.getString(R.string.explore_trending_developer_repository_name_description, repositoryName, it))
                    descriptionSpannable.setSpan(foregroundColorSpan, 0, repositoryName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    descriptionSpannable
                } ?: repositoryName,
                TextViewCompat.getTextMetricsParams(textView),
                null
        ))
    }

}