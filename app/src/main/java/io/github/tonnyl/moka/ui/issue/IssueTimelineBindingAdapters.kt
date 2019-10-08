package io.github.tonnyl.moka.ui.issue

import android.graphics.Color
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateUtils
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.type.LockReason
import io.github.tonnyl.moka.util.*

@BindingAdapter("issueTimelineEventContentTextFuture")
fun AppCompatTextView.issueTimelineEventContentTextFuture(
    event: IssueTimelineItem?
) {
    val content = when (event) {
        is AddedToProjectEvent -> {
            context.getString(R.string.issue_timeline_added_to_project)
        }
        is AssignedEvent -> {
            if (event.assigneeLogin == event.assigneeLogin) {
                context.getString(R.string.issue_timeline_assigned_event_self_assigned)
            } else {
                context.getString(
                    R.string.issue_timeline_assigned_event_assigned_someone,
                    event.assigneeLogin
                ).toHtmlInLegacyMode()
            }
        }
        is ClosedEvent -> {
            context.getString(R.string.issue_timeline_closed_event_closed)
        }
        is ConvertedNoteToIssueEvent -> {
            context.getString(R.string.issue_timeline_converted_note_to_issue)
        }
        is CrossReferencedEvent -> {
            context.getString(
                R.string.issue_timeline_cross_referenced_event_cross_referenced,
                event.issue?.title ?: event.pullRequest?.title,
                event.issue?.number ?: event.pullRequest?.number
            ).toHtmlInLegacyMode()
        }
        is DemilestonedEvent -> {
            context.getString(
                R.string.issue_timeline_demilestoned_event_demilestoned,
                event.milestoneTitle
            ).toHtmlInLegacyMode()
        }
        is IssueComment -> {
            if (event.body.isEmpty()) {
                val text = context.getString(R.string.issue_timeline_no_description_provided)
                val spannable = SpannableString(text)
                spannable.setSpan(
                    ForegroundColorSpan(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorTextSecondary,
                            null
                        )
                    ),
                    0,
                    text.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannable
            } else {
                event.body
            }
        }
        is LabeledEvent -> {
            val first = context.getString(R.string.issue_timeline_labeled_event_labeled)
            val second = context.getString(R.string.issue_timeline_label)
            val all = "$first ${event.label.name} $second"
            val spannable = SpannableString(all)
            spannable.setSpan(
                BackgroundColorSpan(event.label.color.toColor() ?: Color.WHITE),
                first.length,
                first.length + event.label.name.length + 2,
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
                first.length + event.label.name.length + 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable
        }
        is LockedEvent -> {
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
            ).toHtmlInLegacyMode()
        }
        is MarkedAsDuplicateEvent -> {
            context.getString(R.string.issue_timeline_marked_as_duplicate)
        }
        is MilestonedEvent -> {
            context.getString(
                R.string.issue_timeline_milestoned_event_milestoned,
                event.milestoneTitle
            ).toHtmlInLegacyMode()
        }
        is MovedColumnsInProjectEvent -> {
            context.getString(R.string.issue_timeline_moved_columns_in_project)
        }
        is PinnedEvent -> {
            context.getString(R.string.issue_timeline_pinned)
        }
        is ReferencedEvent -> {
            context.getString(
                R.string.issue_timeline_referenced_event_referenced,
                event.issue?.title ?: event.pullRequest?.title,
                event.issue?.number ?: event.pullRequest?.number
            ).toHtmlInLegacyMode()
        }
        is RemovedFromProjectEvent -> {
            context.getString(R.string.issue_timeline_removed_from_project)
        }
        is RenamedTitleEvent -> {
            context.getString(
                R.string.issue_timeline_renamed_title_event_change_title,
                event.previousTitle,
                event.currentTitle
            ).toHtmlInLegacyMode()
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
            val assigneeLogin = event.assignee.assigneeLogin
            if (event.actor?.login == assigneeLogin) {
                context.getString(R.string.issue_timeline_unassigned_event_self_unassigned)
            } else {
                context.getString(
                    R.string.issue_timeline_assigned_event_assigned_someone,
                    assigneeLogin
                ).toHtmlInLegacyMode()
            }
        }
        is UnlabeledEvent -> {
            val first = context.getString(R.string.issue_timeline_unlabeled_event_unlabeled)
            val second = context.getString(R.string.issue_timeline_label)
            val all = "$first ${event.label.name} $second"
            val spannable = SpannableString(all)
            spannable.setSpan(
                BackgroundColorSpan(event.label.color.toColor() ?: Color.WHITE),
                first.length,
                first.length + event.label.name.length + 2,
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
                first.length + event.label.name.length + 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable
        }
        is UnlockedEvent -> {
            context.getString(R.string.issue_timeline_unlocked_event_unlocked)
        }
        is UnpinnedEvent -> {
            context.getString(R.string.issue_timeline_unpinned)
        }
        else -> {
            ""
        }
    }

    textFuture(content)
}

@BindingAdapter("issueTimelineEventAvatar")
fun AppCompatImageView.issueTimelineEventAvatar(
    event: IssueTimelineItem?
) {
    val avatarUrl: Uri? = when (event) {
        is AddedToProjectEvent -> {
            event.actor?.avatarUrl
        }
        is AssignedEvent -> {
            event.actor?.avatarUrl
        }
        is ClosedEvent -> {
            event.actor?.avatarUrl
        }
        is ConvertedNoteToIssueEvent -> {
            event.actor?.avatarUrl
        }
        is CrossReferencedEvent -> {
            event.actor?.avatarUrl
        }
        is DemilestonedEvent -> {
            event.actor?.avatarUrl
        }
        is IssueComment -> {
            event.author?.avatarUrl
        }
        is LabeledEvent -> {
            event.actor?.avatarUrl
        }
        is LockedEvent -> {
            event.actor?.avatarUrl
        }
        is MarkedAsDuplicateEvent -> {
            event.actor?.avatarUrl
        }
        is MilestonedEvent -> {
            event.actor?.avatarUrl
        }
        is MovedColumnsInProjectEvent -> {
            event.actor?.avatarUrl
        }
        is PinnedEvent -> {
            event.actor?.avatarUrl
        }
        is ReferencedEvent -> {
            event.actor?.avatarUrl
        }
        is RemovedFromProjectEvent -> {
            event.actor?.avatarUrl
        }
        is RenamedTitleEvent -> {
            event.actor?.avatarUrl
        }
        is ReopenedEvent -> {
            event.actor?.avatarUrl
        }
        is TransferredEvent -> {
            event.actor?.avatarUrl
        }
        is UnassignedEvent -> {
            event.actor?.avatarUrl
        }
        is UnlabeledEvent -> {
            event.actor?.avatarUrl
        }
        is UnlockedEvent -> {
            event.actor?.avatarUrl
        }
        is UnpinnedEvent -> {
            event.actor?.avatarUrl
        }
        else -> {
            null
        }
    }

    avatarUrl(avatarUrl)
}

@BindingAdapter("issueTimelineEventLogin")
fun AppCompatTextView.issueTimelineEventLogin(
    event: IssueTimelineItem?
) {
    val login = when (event) {
        is AddedToProjectEvent -> {
            event.actor?.login
        }
        is AssignedEvent -> {
            event.actor?.login
        }
        is ClosedEvent -> {
            event.actor?.login
        }
        is ConvertedNoteToIssueEvent -> {
            event.actor?.login
        }
        is CrossReferencedEvent -> {
            event.actor?.login
        }
        is DemilestonedEvent -> {
            event.actor?.login
        }
        is IssueComment -> {
            event.author?.login
        }
        is LabeledEvent -> {
            event.actor?.login
        }
        is LockedEvent -> {
            event.actor?.login
        }
        is MarkedAsDuplicateEvent -> {
            event.actor?.login
        }
        is MilestonedEvent -> {
            event.actor?.login
        }
        is MovedColumnsInProjectEvent -> {
            event.actor?.login
        }
        is PinnedEvent -> {
            event.actor?.login
        }
        is ReferencedEvent -> {
            event.actor?.login
        }
        is RemovedFromProjectEvent -> {
            event.actor?.login
        }
        is RenamedTitleEvent -> {
            event.actor?.login
        }
        is ReopenedEvent -> {
            event.actor?.login
        }
        is TransferredEvent -> {
            event.actor?.login
        }
        is UnassignedEvent -> {
            event.actor?.login
        }
        is UnlabeledEvent -> {
            event.actor?.login
        }
        is UnlockedEvent -> {
            event.actor?.login
        }
        is UnpinnedEvent -> {
            event.actor?.login
        }
        else -> {
            null
        }
    }

    textFuture(login ?: "")
}

@BindingAdapter("issueTimelineEventCreatedAt")
fun AppCompatTextView.issueTimelineEventCreatedAt(
    event: IssueTimelineItem?
) {
    val createdAt = when (event) {
        is AddedToProjectEvent -> {
            event.createdAt
        }
        is AssignedEvent -> {
            event.createdAt
        }
        is ClosedEvent -> {
            event.createdAt
        }
        is ConvertedNoteToIssueEvent -> {
            event.createdAt
        }
        is CrossReferencedEvent -> {
            event.createdAt
        }
        is DemilestonedEvent -> {
            event.createdAt
        }
        is IssueComment -> {
            event.createdAt
        }
        is LabeledEvent -> {
            event.createdAt
        }
        is LockedEvent -> {
            event.createdAt
        }
        is MarkedAsDuplicateEvent -> {
            event.createdAt
        }
        is MilestonedEvent -> {
            event.createdAt
        }
        is MovedColumnsInProjectEvent -> {
            event.createdAt
        }
        is PinnedEvent -> {
            event.createdAt
        }
        is ReferencedEvent -> {
            event.createdAt
        }
        is RemovedFromProjectEvent -> {
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
        is UnpinnedEvent -> {
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
    event: IssueTimelineItem?
) {
    val iconResId: Int?
    val backgroundResId: Int?
    when (event) {
        is AddedToProjectEvent -> {
            iconResId = R.drawable.ic_dashboard_outline
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is AssignedEvent -> {
            iconResId = R.drawable.ic_person_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is ClosedEvent -> {
            iconResId = R.drawable.ic_pr_issue_close_24
            backgroundResId = R.drawable.bg_issue_timeline_event_2
        }
        is ConvertedNoteToIssueEvent -> {
            iconResId = R.drawable.ic_issue_open_24
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
        is MarkedAsDuplicateEvent -> {
            iconResId = R.drawable.ic_copy_24dp
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is MilestonedEvent -> {
            iconResId = R.drawable.ic_milestone_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is MovedColumnsInProjectEvent -> {
            iconResId = R.drawable.ic_dashboard_outline
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is PinnedEvent -> {
            iconResId = R.drawable.ic_pin_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is ReferencedEvent -> {
            iconResId = R.drawable.ic_bookmark_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
        }
        is RemovedFromProjectEvent -> {
            iconResId = R.drawable.ic_dashboard_outline
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
        is UnpinnedEvent -> {
            iconResId = R.drawable.ic_pin_24
            backgroundResId = R.drawable.bg_issue_timeline_event_1
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