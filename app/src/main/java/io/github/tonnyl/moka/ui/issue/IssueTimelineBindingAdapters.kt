package io.github.tonnyl.moka.ui.issue

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateUtils
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.LockReason
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.util.avatarUrl
import io.github.tonnyl.moka.util.backgroundResId
import io.github.tonnyl.moka.util.imageResId
import io.github.tonnyl.moka.util.textFuture

@BindingAdapter("issueTimelineEventContentTextFuture")
fun AppCompatTextView.issueTimelineEventContentTextFuture(
    event: IssueTimelineItem
) {
    val content = when (event) {
        is AssignedEvent -> {
            if (event.assigneeLogin == event.assigneeLogin) {
                context.getString(R.string.issue_timeline_assigned_event_self_assigned)
            } else {
                HtmlCompat.fromHtml(
                    context.getString(
                        R.string.issue_timeline_assigned_event_assigned_someone,
                        event.assigneeLogin
                    ), HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }
        is ClosedEvent -> {
            context.getString(R.string.issue_timeline_closed_event_closed)
        }
        is CommitEvent -> {
            event.message
        }
        is CrossReferencedEvent -> {
            HtmlCompat.fromHtml(
                context.getString(
                    R.string.issue_timeline_cross_referenced_event_cross_referenced,
                    event.issue?.title ?: event.pullRequest?.title,
                    event.issue?.number ?: event.pullRequest?.number
                ), HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        is DemilestonedEvent -> {
            HtmlCompat.fromHtml(
                context.getString(
                    R.string.issue_timeline_demilestoned_event_demilestoned,
                    event.milestoneTitle
                ), HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        is LabeledEvent -> {
            val first = context.getString(R.string.issue_timeline_labeled_event_labeled)
            val second = context.getString(R.string.issue_timeline_label)
            val all = "$first ${event.labelName} $second"
            val spannable = SpannableString(all)
            spannable.setSpan(
                BackgroundColorSpan(Color.parseColor("#${event.labelColor}")),
                first.length,
                first.length + event.labelName.length + 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                ForegroundColorSpan(
                    ResourcesCompat.getColor(
                        context.resources,
                        R.color.issue_pr_label_text,
                        null
                    )
                ),
                first.length,
                first.length + event.labelName.length + 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable
        }
        is LockedEvent -> {
            HtmlCompat.fromHtml(
                context.getString(
                    R.string.issue_timeline_locked_event_locked_as,
                    context.getString(
                        when (event.lockReason) {
                            LockReason.OFF_TOPIC -> R.string.issue_lock_reason_off_topic
                            LockReason.RESOLVED -> R.string.issue_lock_reason_resolved
                            LockReason.SPAM -> R.string.issue_lock_reason_spam
                            LockReason.TOO_HEATED -> R.string.issue_lock_reason_too_heated
                            else -> R.string.issue_lock_reason_too_heated
                        }
                    )
                ), HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        is MilestonedEvent -> {
            HtmlCompat.fromHtml(
                context.getString(
                    R.string.issue_timeline_milestoned_event_milestoned,
                    event.milestoneTitle
                ), HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        is ReferencedEvent -> {
            HtmlCompat.fromHtml(
                context.getString(
                    R.string.issue_timeline_referenced_event_referenced,
                    event.issue?.title ?: event.pullRequest?.title,
                    event.issue?.number ?: event.pullRequest?.number
                ), HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        is RenamedTitleEvent -> {
            HtmlCompat.fromHtml(
                context.getString(
                    R.string.issue_timeline_renamed_title_event_change_title,
                    event.previousTitle,
                    event.currentTitle
                ), HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        is ReopenedEvent -> {
            context.getString(R.string.issue_timeline_reopened_event_reopened)
        }
        is TransferredEvent -> {
            context.getString(
                R.string.issue_timeline_transferred_event_transferred,
                event.nameWithOwnerOfFromRepository
            )
        }
        is UnassignedEvent -> {
            if (event.assigneeLogin == event.assigneeLogin) {
                context.getString(R.string.issue_timeline_unassigned_event_self_unassigned)
            } else {
                HtmlCompat.fromHtml(
                    context.getString(
                        R.string.issue_timeline_assigned_event_assigned_someone,
                        event.assigneeLogin
                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }
        is UnlabeledEvent -> {
            val first = context.getString(R.string.issue_timeline_unlabeled_event_unlabeled)
            val second = context.getString(R.string.issue_timeline_label)
            val all = "$first ${event.labelName} $second"
            val spannable = SpannableString(all)
            spannable.setSpan(
                BackgroundColorSpan(Color.parseColor("#${event.labelColor}")),
                first.length,
                first.length + event.labelName.length + 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                ForegroundColorSpan(
                    ResourcesCompat.getColor(
                        context.resources,
                        R.color.issue_pr_label_text,
                        null
                    )
                ),
                first.length,
                first.length + event.labelName.length + 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable
        }
        is UnlockedEvent -> {
            context.getString(R.string.issue_timeline_unlocked_event_unlocked)
        }
        else -> {
            ""
        }
    }

    textFuture(content)
}

@BindingAdapter("issueTimelineEventAvatar")
fun AppCompatImageView.issueTimelineEventAvatar(
    event: IssueTimelineItem
) {
    val avatarUrl = when (event) {
        is AssignedEvent -> {
            event.actorAvatarUrl?.toString()
        }
        is ClosedEvent -> {
            event.avatarUrl?.toString()
        }
        is CommitEvent -> {
            event.authorAvatarUrl?.toString() ?: event.committerAvatarUri?.toString()
        }
        is CrossReferencedEvent -> {
            event.avatarUrl?.toString()
        }
        is DemilestonedEvent -> {
            event.avatarUrl?.toString()
        }
        is LabeledEvent -> {
            event.avatarUrl?.toString()
        }
        is LockedEvent -> {
            event.avatarUrl?.toString()
        }
        is MilestonedEvent -> {
            event.avatarUrl?.toString()
        }
        is ReferencedEvent -> {
            event.avatarUrl?.toString()
        }
        is RenamedTitleEvent -> {
            event.avatarUrl?.toString()
        }
        is ReopenedEvent -> {
            event.avatarUrl?.toString()
        }
        is TransferredEvent -> {
            event.avatarUrl?.toString()
        }
        is UnassignedEvent -> {
            event.actorAvatarUrl?.toString()
        }
        is UnlabeledEvent -> {
            event.avatarUrl?.toString()
        }
        is UnlockedEvent -> {
            event.avatarUrl?.toString()
        }
        else -> {
            null
        }
    }

    avatarUrl(avatarUrl)
}

@BindingAdapter("issueTimelineEventLogin")
fun AppCompatTextView.issueTimelineEventLogin(
    event: IssueTimelineItem
) {
    val login = when (event) {
        is AssignedEvent -> {
            event.actorLogin
        }
        is ClosedEvent -> {
            event.login
        }
        is CommitEvent -> {
            event.authorLogin ?: event.committerLogin
        }
        is CrossReferencedEvent -> {
            event.login
        }
        is DemilestonedEvent -> {
            event.login
        }
        is LabeledEvent -> {
            event.login
        }
        is LockedEvent -> {
            event.login
        }
        is MilestonedEvent -> {
            event.login
        }
        is ReferencedEvent -> {
            event.login
        }
        is RenamedTitleEvent -> {
            event.login
        }
        is ReopenedEvent -> {
            event.login
        }
        is TransferredEvent -> {
            event.login
        }
        is UnassignedEvent -> {
            event.actorLogin
        }
        is UnlabeledEvent -> {
            event.login
        }
        is UnlockedEvent -> {
            event.login
        }
        else -> {
            null
        }
    }

    textFuture(login ?: "")
}

@BindingAdapter("issueTimelineEventCreatedAt")
fun AppCompatTextView.issueTimelineEventCreatedAt(
    event: IssueTimelineItem
) {
    val createdAt = when (event) {
        is AssignedEvent -> {
            event.createdAt
        }
        is ClosedEvent -> {
            event.createdAt
        }
        is CommitEvent -> {
            null
        }
        is CrossReferencedEvent -> {
            event.createdAt
        }
        is DemilestonedEvent -> {
            event.createdAt
        }
        is LabeledEvent -> {
            event.createdAt
        }
        is LockedEvent -> {
            event.createdAt
        }
        is MilestonedEvent -> {
            event.createdAt
        }
        is ReferencedEvent -> {
            event.createdAt
        }
        is RenamedTitleEvent -> {
            event.createdAt
        }
        is ReopenedEvent -> {
            event.createdAt
        }
        is TransferredEvent -> {
            event.createdAt
        }
        is UnassignedEvent -> {
            event.createdAt
        }
        is UnlabeledEvent -> {
            event.createdAt
        }
        is UnlockedEvent -> {
            event.createdAt
        }
        else -> {
            null
        }
    }

    textFuture(
        createdAt?.time?.let {
            DateUtils.getRelativeTimeSpanString(
                it, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS
            )
        } ?: ""
    )
}

@BindingAdapter("issueTimelineEventBgAndIconResId")
fun AppCompatImageView.issueTimelineEventBgAndIconResId(
    event: IssueTimelineItem
) {
    val iconResId: Int?
    val backgroundResId: Int?
    when (event) {
        is AssignedEvent -> {
            iconResId = R.drawable.ic_person_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is ClosedEvent -> {
            iconResId = R.drawable.ic_pr_issue_close_24
            backgroundResId = R.drawable.bg_issue_timeline_event_2
        }
        is CommitEvent -> {
            iconResId = R.drawable.ic_commit_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is CrossReferencedEvent -> {
            iconResId = R.drawable.ic_bookmark_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is DemilestonedEvent -> {
            iconResId = R.drawable.ic_milestone_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is LabeledEvent -> {
            iconResId = R.drawable.ic_label_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is LockedEvent -> {
            iconResId = R.drawable.ic_lock_outline_24dp
            backgroundResId = R.drawable.bg_issue_timeline_event_2
        }
        is MilestonedEvent -> {
            iconResId = R.drawable.ic_milestone_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is ReferencedEvent -> {
            iconResId = R.drawable.ic_bookmark_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is RenamedTitleEvent -> {
            iconResId = R.drawable.ic_edit_white_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is ReopenedEvent -> {
            iconResId = R.drawable.ic_dot_24
            backgroundResId = R.drawable.bg_issue_timeline_event_3
        }
        is TransferredEvent -> {
            iconResId = R.drawable.ic_dot_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is UnassignedEvent -> {
            iconResId = R.drawable.ic_person_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is UnlabeledEvent -> {
            iconResId = R.drawable.ic_label_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is UnlockedEvent -> {
            iconResId = R.drawable.ic_key_24
            backgroundResId = R.drawable.bg_issue_timeline_event_2
        }
        else -> {
            iconResId = null
            backgroundResId = null
        }
    }

    iconResId?.let {
        imageResId(it)
    }

    backgroundResId?.let {
        backgroundResId(it)
    }

}