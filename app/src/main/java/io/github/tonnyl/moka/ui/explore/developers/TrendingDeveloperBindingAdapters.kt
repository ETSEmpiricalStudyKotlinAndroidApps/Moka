package io.github.tonnyl.moka.ui.explore.developers

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.util.textFuture

@BindingAdapter("trendingRank")
fun AppCompatTextView.trendingRank(
    rank: Int
) {
    textFuture(rank.toString())
}

@BindingAdapter(
    value = ["foregroundColorSpan", "trendingDeveloperLogin", "trendingDeveloperName"],
    requireAll = true
)
fun AppCompatTextView.trendingDeveloperName(
    foregroundColorSpan: ForegroundColorSpan?,
    login: String?,
    name: String?
) {
    foregroundColorSpan ?: return
    login ?: return
    name ?: return

    textFuture(
        SpannableStringBuilder(
            context.getString(
                R.string.explore_trending_developer_name,
                login,
                name
            )
        ).apply {
            setSpan(
                foregroundColorSpan,
                length - name.length - 2,
                length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    )
}

@BindingAdapter(
    value = ["foregroundColorSpan", "trendingDeveloperRepositoryName", "trendingDeveloperRepositoryDescription"],
    requireAll = true
)
fun AppCompatTextView.trendingDeveloperDescription(
    foregroundColorSpan: ForegroundColorSpan?,
    repositoryName: String?,
    repositoryDescription: String?
) {
    foregroundColorSpan ?: return
    repositoryName ?: return
    repositoryDescription ?: return

    textFuture(SpannableStringBuilder(
        context.getString(
            R.string.explore_trending_developer_repository_name_description,
            repositoryName,
            repositoryDescription
        )
    ).apply {
        setSpan(
            foregroundColorSpan,
            0,
            repositoryName.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    })
}