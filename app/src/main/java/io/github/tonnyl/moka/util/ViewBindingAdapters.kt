package io.github.tonnyl.moka.util

import android.net.Uri
import android.text.format.DateUtils
import android.text.method.MovementMethod
import android.view.View
import android.view.View.*
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Issue
import io.github.tonnyl.moka.data.PullRequest
import io.github.tonnyl.moka.network.GlideLoader
import io.github.tonnyl.moka.type.CommentAuthorAssociation
import io.github.tonnyl.moka.widget.ThemedWebView

// ===== View start =====
@BindingAdapter("invisibleUnless")
fun View.invisibleUnless(visible: Boolean) {
    visibility = if (visible) VISIBLE else INVISIBLE
}

@BindingAdapter("goneUnless")
fun View.goneUnless(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}

@BindingAdapter("visibleUnless")
fun View.visibleUnless(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}

@BindingAdapter("visibleOrGone")
fun View.visibleOrGone(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}
// ===== View end =====

// ===== AppCompatImageView start =====
@BindingAdapter("avatarUrl")
fun AppCompatImageView.avatarUrl(
    url: String?
) {
    GlideLoader.loadAvatar(url, this)
}

@BindingAdapter("avatarUrl")
fun AppCompatImageView.avatarUrl(
    url: Uri?
) {
    avatarUrl(url?.toString())
}

@BindingAdapter("imageResId")
fun AppCompatImageView.imageResId(
    @DrawableRes drawableResId: Int
) {
    setImageResource(drawableResId)
}

@BindingAdapter("backgroundResId")
fun AppCompatImageView.backgroundResId(
    @DrawableRes backgroundResId: Int
) {
    setBackgroundResource(backgroundResId)
}
// ===== AppCompatImageView end =====

// ===== AppCompatTextView start =====
@BindingAdapter("textFuture")
fun AppCompatTextView.textFuture(
    text: CharSequence?
) {
    text ?: return
    setTextFuture(
        PrecomputedTextCompat.getTextFuture(
            text,
            TextViewCompat.getTextMetricsParams(this),
            null
        )
    )
}

@BindingAdapter("intOrZero")
fun AppCompatTextView.intOrZero(
    value: Int?
) {
    text = value?.toString() ?: "0"
}

@BindingAdapter("authorAssociation")
fun AppCompatTextView.authorAssociation(
    association: CommentAuthorAssociation?
) {
    val stringResId = when (association) {
        CommentAuthorAssociation.COLLABORATOR -> R.string.author_association_collaborator
        CommentAuthorAssociation.CONTRIBUTOR -> R.string.author_association_contributor
        CommentAuthorAssociation.FIRST_TIMER -> R.string.author_association_first_timer
        CommentAuthorAssociation.FIRST_TIME_CONTRIBUTOR -> R.string.author_association_first_timer_contributor
        CommentAuthorAssociation.MEMBER -> R.string.author_association_member
        CommentAuthorAssociation.OWNER -> R.string.author_association_owner
        // including CommentAuthorAssociation.NONE and null
        else -> -1
    }

    textFuture(if (stringResId != -1) resources.getString(stringResId) else "")
}

@BindingAdapter("issueInfo")
fun AppCompatTextView.issueInfo(issue: Issue?) {
    issue ?: return
    val status = context.getString(
        if (issue.closed) {
            R.string.issue_pr_status_closed
        } else {
            R.string.issue_pr_status_open
        }
    )
    val author = context.getString(R.string.issue_pr_created_by, issue.author?.login)
    val time = DateUtils.getRelativeTimeSpanString(
        issue.createdAt.time,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    )

    text = context.getString(R.string.issue_pr_info_format, status, author, time)
}

@BindingAdapter("prInfo")
fun AppCompatTextView.prInfo(pr: PullRequest?) {
    pr ?: return

    val status = context.getString(
        when {
            pr.closed -> R.string.issue_pr_status_closed
            pr.merged -> R.string.issue_pr_status_merged
            else -> R.string.issue_pr_status_open
        }
    )
    val author = context.getString(R.string.issue_pr_created_by, pr.author?.login)
    val time = DateUtils.getRelativeTimeSpanString(
        pr.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS
    )

    text = context.getString(R.string.issue_pr_info_format, status, author, time)
}

@BindingAdapter("movementMethod")
fun AppCompatTextView.movementMethod(
    movement: MovementMethod
) {
    movementMethod = movement
}
// ===== AppCompatTextView end =====

// ===== AppCompatButton start =====
@BindingAdapter("stringResId")
fun AppCompatButton.stringResId(
    @StringRes id: Int
) {
    text = context.getString(id)
}
// ===== AppCompatButton end =====

// ===== SwipeRefreshLayout start =====
@BindingAdapter("colorSchemaColors")
fun SwipeRefreshLayout.colorSchemaColors(
    colorResIds: IntArray
) {
    setColorSchemeColors(*colorResIds)
}

@BindingAdapter("bindRefreshing")
fun SwipeRefreshLayout.bindRefreshing(refreshing: Boolean) {
    post {
        isRefreshing = refreshing
    }
}

@BindingAdapter("stopRefreshing")
fun SwipeRefreshLayout.stopRefreshing(stopRefreshingWhen: Boolean) {
    if (isRefreshing && stopRefreshingWhen) {
        post {
            isRefreshing = false
        }
    }
}

@BindingAdapter("onRefresh")
fun SwipeRefreshLayout.onRefresh(action: (() -> Unit)?) {
    setOnRefreshListener {
        action?.invoke()
    }
}
// ===== SwipeRefreshLayout end =====

// ===== RecyclerView start =====
@BindingAdapter("itemHasFixedSize")
fun RecyclerView.itemHasFixedSize(hasFixedSize: Boolean) {
    setHasFixedSize(hasFixedSize)
}

// ===== RecyclerView end =====
@BindingAdapter("htmlContent")
fun ThemedWebView.htmlContent(
    htmlContent: String?
) {
    loadData(htmlContent ?: "")
}