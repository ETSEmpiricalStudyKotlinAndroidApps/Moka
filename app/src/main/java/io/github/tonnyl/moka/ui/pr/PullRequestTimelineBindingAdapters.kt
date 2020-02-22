package io.github.tonnyl.moka.ui.pr

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateUtils
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.type.LockReason
import io.github.tonnyl.moka.type.PullRequestReviewState
import io.github.tonnyl.moka.util.*

@BindingAdapter("prTimelineItemContentTextFuture")
fun AppCompatTextView.prTimelineItemContentTextFuture(
    data: PullRequestTimelineItem?
) {
    val content = when (data) {
        is AddedToProjectEvent -> {
            context.getString(R.string.issue_timeline_added_to_project)
        }
        is AssignedEvent -> {
            if (data.actor?.login == data.assigneeLogin) {
                context.getString(R.string.issue_timeline_assigned_event_self_assigned)
            } else {
                context.getString(
                    R.string.issue_timeline_assigned_event_assigned_someone,
                    data.assigneeLogin
                ).toHtmlInLegacyMode()
            }
        }
        is BaseRefChangedEvent -> {
            context.getString(R.string.pull_request_base_ref_changed)
        }
        is BaseRefForcePushedEvent -> {
            context.getString(
                R.string.pull_request_force_pushed_branch,
                data.ref?.name,
                data.beforeCommit?.oid?.toShortOid(),
                data.afterCommit?.oid?.toShortOid()
            )
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
                data.issue?.title ?: data.pullRequest?.title,
                data.issue?.number ?: data.pullRequest?.number
            ).toHtmlInLegacyMode()
        }
        is DemilestonedEvent -> {
            context.getString(
                R.string.issue_timeline_demilestoned_event_demilestoned, data.milestoneTitle
            ).toHtmlInLegacyMode()
        }
        is DeployedEvent -> {
            context.getString(
                R.string.pull_request_deployed_to_branch, data.deploymentEnvironment
            ).toHtmlInLegacyMode()
        }
        is DeploymentEnvironmentChangedEvent -> {
            context.getString(
                R.string.pull_request_changed_deploy_environment, data.deployment.environment
            ).toHtmlInLegacyMode()
        }
        is HeadRefDeletedEvent -> {
            context.getString(
                R.string.pull_request_deleted_branch, data.headRefName
            )
        }
        is HeadRefForcePushedEvent -> {
            context.getString(
                R.string.pull_request_force_pushed_branch,
                data.ref?.name,
                data.beforeCommitOid.toShortOid(),
                data.afterCommitOid.toShortOid()
            )
        }
        is HeadRefRestoredEvent -> {
            context.getString(R.string.pull_request_restore_branch, data.pullRequestHeadRefName)
        }
        is IssueComment -> {
            if (data.body.isEmpty()) {
                context.getString(R.string.issue_timeline_no_description_provided)
            } else {
                data.body
            }
        }
        is LockedEvent -> {
            context.getString(
                R.string.issue_timeline_locked_event_locked_as,
                context.getString(
                    when (data.lockReason) {
                        LockReason.OFF_TOPIC -> R.string.issue_lock_reason_off_topic
                        LockReason.RESOLVED -> R.string.issue_lock_reason_resolved
                        LockReason.SPAM -> R.string.issue_lock_reason_spam
                        LockReason.TOO_HEATED -> R.string.issue_lock_reason_too_heated
                        else -> R.string.issue_lock_reason_too_heated
                    }
                )
            ).toHtmlInLegacyMode()
        }
        is LabeledEvent -> {
            val first = context.getString(R.string.issue_timeline_labeled_event_labeled)
            val second = context.getString(R.string.issue_timeline_label)
            val all = "$first ${data.label.name} $second"
            val spannable = SpannableString(all)
            spannable.setSpan(
                BackgroundColorSpan(data.label.color.toColor() ?: Color.WHITE),
                first.length,
                first.length + data.label.name.length + 2,
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
                first.length + data.label.name.length + 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable
        }
        is MarkedAsDuplicateEvent -> {
            context.getString(R.string.issue_timeline_marked_as_duplicate)
        }
        is MergedEvent -> {
            context.getString(
                R.string.pull_request_merged_commit,
                data.commitOid.toShortOid(),
                data.mergeRefName
            ).toHtmlInLegacyMode()
        }
        is MilestonedEvent -> {
            context.getString(
                R.string.issue_timeline_milestoned_event_milestoned,
                data.milestoneTitle
            ).toHtmlInLegacyMode()
        }
        is MovedColumnsInProjectEvent -> {
            context.getString(R.string.issue_timeline_moved_columns_in_project)
        }
        is PinnedEvent -> {
            context.getString(R.string.issue_timeline_pinned)
        }
        is PullRequestCommit -> {
            data.commit.message
        }
        is PullRequestReview -> {
            var reviewStateText: String = when (data.state) {
                PullRequestReviewState.APPROVED -> {
                    context.getString(R.string.pull_request_review_approved_changes)
                }
                PullRequestReviewState.CHANGES_REQUESTED -> {
                    context.getString(R.string.pull_request_review_request_changes)
                }
                // PENDING, DISMISSED, COMMENTED
                else -> {
                    context.getString(R.string.pull_request_reviewed)
                }
            }

            if (data.body.isNotEmpty()) {
                reviewStateText += context.getString(
                    R.string.pull_request_and_left_a_comment,
                    data.body
                ).toHtmlInLegacyMode()
            }

            reviewStateText
        }
        is ReadyForReviewEvent -> {
            context.getString(R.string.pull_request_marked_as_ready_for_review)
        }
        is ReferencedEvent -> {
            context.getString(
                R.string.issue_timeline_referenced_event_referenced,
                data.issue?.title ?: data.pullRequest?.title,
                data.issue?.number ?: data.pullRequest?.number
            ).toHtmlInLegacyMode()
        }
        is RemovedFromProjectEvent -> {
            context.getString(R.string.pull_request_removed_from_project)
        }
        is RenamedTitleEvent -> {
            context.getString(
                R.string.issue_timeline_renamed_title_event_change_title,
                data.previousTitle,
                data.currentTitle
            ).toHtmlInLegacyMode()
        }
        is ReopenedEvent -> {
            context.getString(R.string.issue_timeline_reopened_event_reopened)
        }
        is ReviewDismissedEvent -> {
            if (data.dismissalMessage.isNullOrEmpty()) {
                context.getString(
                    R.string.pull_request_dismiss_someones_review_and_left_a_comment,
                    data.review?.author?.login,
                    data.dismissalMessage
                ).toHtmlInLegacyMode()
            } else {
                context.getString(
                    R.string.pull_request_dismiss_someones_review, data.review?.author?.login
                ).toHtmlInLegacyMode()
            }
        }
        is ReviewRequestRemovedEvent -> {
            context.getString(
                R.string.pull_request_removed_someones_review_request,
                data.requestedReviewerUser?.login
                    ?: data.requestedReviewerTeam?.combinedSlug
                    ?: data.requestedReviewerMannequin?.login
            ).toHtmlInLegacyMode()
        }
        is ReviewRequestedEvent -> {
            if (data.actor?.login == data.requestedReviewerLogin) {
                context.getString(R.string.pull_request_self_requested_a_review)
            } else {
                context.getString(
                    R.string.pull_request_requested_review_from,
                    data.requestedReviewerLogin
                ).toHtmlInLegacyMode()
            }
        }
        is TransferredEvent -> {
            context.getString(
                R.string.issue_timeline_transferred_event_transferred,
                data.nameWithOwnerOfFromRepository
            )
        }
        is UnassignedEvent -> {
            val assigneeLogin = data.assignee.assigneeLogin
            if (data.actor?.login == assigneeLogin) {
                context.getString(R.string.issue_timeline_unassigned_event_self_unassigned)
            } else {
                context.getString(
                    R.string.issue_timeline_unassigned_event_unassigned_someone,
                    assigneeLogin
                ).toHtmlInLegacyMode()
            }
        }
        is UnlabeledEvent -> {
            val first = context.getString(R.string.issue_timeline_unlabeled_event_unlabeled)
            val second = context.getString(R.string.issue_timeline_label)
            val all = "$first ${data.label.name} $second"
            val spannable = SpannableString(all)
            spannable.setSpan(
                BackgroundColorSpan(data.label.color.toColor() ?: Color.WHITE),
                first.length,
                first.length + data.label.name.length + 2,
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
                first.length + data.label.name.length + 2,
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
            null
        }
    }

    if (content.isNullOrEmpty()) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
        textFuture(content)
    }
}

@BindingAdapter("prTimelineItemLoginTextFuture")
fun AppCompatTextView.prTimelineItemLoginTextFuture(
    data: PullRequestTimelineItem?
) {
    val login = when (data) {
        is AddedToProjectEvent -> {
            data.actor?.login
        }
        is AssignedEvent -> {
            data.actor?.login
        }
        is BaseRefChangedEvent -> {
            data.actor?.login
        }
        is BaseRefForcePushedEvent -> {
            data.actor?.login
        }
        is ClosedEvent -> {
            data.actor?.login
        }
        is ConvertedNoteToIssueEvent -> {
            data.actor?.login
        }
        is CrossReferencedEvent -> {
            data.actor?.login
        }
        is DemilestonedEvent -> {
            data.actor?.login
        }
        is DeployedEvent -> {
            data.actor?.login
        }
        is DeploymentEnvironmentChangedEvent -> {
            data.actor?.login
        }
        is HeadRefDeletedEvent -> {
            data.actor?.login
        }
        is HeadRefForcePushedEvent -> {
            data.actor?.login
        }
        is HeadRefRestoredEvent -> {
            data.actor?.login
        }
        is IssueComment -> {
            data.author?.login
        }
        is LabeledEvent -> {
            data.actor?.login
        }
        is LockedEvent -> {
            data.actor?.login
        }
        is MarkedAsDuplicateEvent -> {
            data.actor?.login
        }
        is MergedEvent -> {
            data.actor?.login
        }
        is MilestonedEvent -> {
            data.actor?.login
        }
        is MovedColumnsInProjectEvent -> {
            data.actor?.login
        }
        is PinnedEvent -> {
            data.actor?.login
        }
        is PullRequestCommit -> {
            data.commit.author?.user?.login
                ?: data.commit.author?.name
                ?: data.commit.committer?.user?.login
                ?: data.commit.committer?.name
        }
        is PullRequestReview -> {
            data.author?.login
        }
        is ReadyForReviewEvent -> {
            data.actor?.login
        }
        is ReferencedEvent -> {
            data.actor?.login
        }
        is RemovedFromProjectEvent -> {
            data.actor?.login
        }
        is RenamedTitleEvent -> {
            data.actor?.login
        }
        is ReopenedEvent -> {
            data.actor?.login
        }
        is ReviewDismissedEvent -> {
            data.actor?.login
        }
        is ReviewRequestRemovedEvent -> {
            data.actor?.login
        }
        is ReviewRequestedEvent -> {
            data.actor?.login
        }
        is UnassignedEvent -> {
            data.actor?.login
        }
        is UnlabeledEvent -> {
            data.actor?.login
        }
        is UnlockedEvent -> {
            data.actor?.login
        }
        is UnpinnedEvent -> {
            data.actor?.login
        }
        else -> {
            null
        }
    }

    textFuture(login ?: "")
}

@BindingAdapter("prTimelineItemAvatar")
fun AppCompatImageView.prTimelineItemAvatar(
    data: PullRequestTimelineItem?
) {
    val avatarUrl = when (data) {
        is AddedToProjectEvent -> {
            data.actor?.avatarUrl
        }
        is AssignedEvent -> {
            data.actor?.avatarUrl
        }
        is BaseRefChangedEvent -> {
            data.actor?.avatarUrl
        }
        is BaseRefForcePushedEvent -> {
            data.actor?.avatarUrl
        }
        is ClosedEvent -> {
            data.actor?.avatarUrl
        }
        is ConvertedNoteToIssueEvent -> {
            data.actor?.avatarUrl
        }
        is CrossReferencedEvent -> {
            data.actor?.avatarUrl
        }
        is DemilestonedEvent -> {
            data.actor?.avatarUrl
        }
        is DeployedEvent -> {
            data.actor?.avatarUrl
        }
        is DeploymentEnvironmentChangedEvent -> {
            data.actor?.avatarUrl
        }
        is HeadRefDeletedEvent -> {
            data.actor?.avatarUrl
        }
        is HeadRefForcePushedEvent -> {
            data.actor?.avatarUrl
        }
        is HeadRefRestoredEvent -> {
            data.actor?.avatarUrl
        }
        is IssueComment -> {
            data.author?.avatarUrl
        }
        is LabeledEvent -> {
            data.actor?.avatarUrl
        }
        is LockedEvent -> {
            data.actor?.avatarUrl
        }
        is MarkedAsDuplicateEvent -> {
            data.actor?.avatarUrl
        }
        is MergedEvent -> {
            data.actor?.avatarUrl
        }
        is MilestonedEvent -> {
            data.actor?.avatarUrl
        }
        is MovedColumnsInProjectEvent -> {
            data.actor?.avatarUrl
        }
        is PinnedEvent -> {
            data.actor?.avatarUrl
        }
        is PullRequestCommit -> {
            data.commit.author?.avatarUrl ?: data.commit.committer?.avatarUrl
        }
        is PullRequestReview -> {
            data.author?.avatarUrl
        }
        is ReadyForReviewEvent -> {
            data.actor?.avatarUrl
        }
        is ReferencedEvent -> {
            data.actor?.avatarUrl
        }
        is RemovedFromProjectEvent -> {
            data.actor?.avatarUrl
        }
        is RenamedTitleEvent -> {
            data.actor?.avatarUrl
        }
        is ReopenedEvent -> {
            data.actor?.avatarUrl
        }
        is ReviewDismissedEvent -> {
            data.actor?.avatarUrl
        }
        is ReviewRequestRemovedEvent -> {
            data.actor?.avatarUrl
        }
        is ReviewRequestedEvent -> {
            data.actor?.avatarUrl
        }
        is UnassignedEvent -> {
            data.actor?.avatarUrl
        }
        is UnlabeledEvent -> {
            data.actor?.avatarUrl
        }
        is UnlockedEvent -> {
            data.actor?.avatarUrl
        }
        is UnpinnedEvent -> {
            data.actor?.avatarUrl
        }
        else -> {
            null
        }
    }

    avatarUrl(avatarUrl)
}

@BindingAdapter("prTimelineItemCreatedAt")
fun AppCompatTextView.prTimelineItemCreatedAt(
    data: PullRequestTimelineItem?
) {
    val createdAt = when (data) {
        is AddedToProjectEvent -> {
            data.createdAt
        }
        is AssignedEvent -> {
            data.createdAt
        }
        is BaseRefChangedEvent -> {
            data.createdAt
        }
        is BaseRefForcePushedEvent -> {
            data.createdAt
        }
        is ClosedEvent -> {
            data.createdAt
        }
        is ConvertedNoteToIssueEvent -> {
            data.createdAt
        }
        is CrossReferencedEvent -> {
            data.createdAt
        }
        is DemilestonedEvent -> {
            data.createdAt
        }
        is DeployedEvent -> {
            data.createdAt
        }
        is DeploymentEnvironmentChangedEvent -> {
            data.createdAt
        }
        is HeadRefDeletedEvent -> {
            data.createdAt
        }
        is HeadRefForcePushedEvent -> {
            data.createdAt
        }
        is HeadRefRestoredEvent -> {
            data.createdAt
        }
        is IssueComment -> {
            data.createdAt
        }
        is LabeledEvent -> {
            data.createdAt
        }
        is LockedEvent -> {
            data.createdAt
        }
        is MarkedAsDuplicateEvent -> {
            data.createdAt
        }
        is MergedEvent -> {
            data.createdAt
        }
        is MilestonedEvent -> {
            data.createdAt
        }
        is MovedColumnsInProjectEvent -> {
            data.createdAt
        }
        is PinnedEvent -> {
            data.createdAt
        }
        is PullRequestCommit -> {
            null
        }
        is PullRequestReview -> {
            data.createdAt
        }
        is ReadyForReviewEvent -> {
            data.createdAt
        }
        is ReferencedEvent -> {
            data.createdAt
        }
        is RemovedFromProjectEvent -> {
            data.createdAt
        }
        is RenamedTitleEvent -> {
            data.createdAt
        }
        is ReopenedEvent -> {
            data.createdAt
        }
        is ReviewDismissedEvent -> {
            data.createdAt
        }
        is ReviewRequestRemovedEvent -> {
            data.createdAt
        }
        is ReviewRequestedEvent -> {
            data.createdAt
        }
        is UnassignedEvent -> {
            data.createdAt
        }
        is UnlabeledEvent -> {
            data.createdAt
        }
        is UnlockedEvent -> {
            data.createdAt
        }
        is UnpinnedEvent -> {
            data.createdAt
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

@BindingAdapter("prTimelineItemIconAndBgResId")
fun AppCompatImageView.prTimelineItemIconAndBgResId(
    data: PullRequestTimelineItem?
) {
    val iconResId: Int?
    val bgResId: Int?
    when (data) {
        is AddedToProjectEvent -> {
            iconResId = R.drawable.ic_dashboard_outline
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is AssignedEvent -> {
            iconResId = R.drawable.ic_person_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is BaseRefChangedEvent,
        is BaseRefForcePushedEvent -> {
            iconResId = R.drawable.ic_book_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is ClosedEvent -> {
            iconResId = R.drawable.ic_pr_issue_close_24
            bgResId = R.drawable.bg_issue_timeline_event_2
        }
        is ConvertedNoteToIssueEvent -> {
            iconResId = R.drawable.ic_issue_open_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is CrossReferencedEvent -> {
            iconResId = R.drawable.ic_bookmark_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is DemilestonedEvent -> {
            iconResId = R.drawable.ic_milestone_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is DeployedEvent,
        is DeploymentEnvironmentChangedEvent -> {
            iconResId = R.drawable.ic_rocket_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is HeadRefDeletedEvent -> {
            iconResId = R.drawable.ic_delete_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is HeadRefForcePushedEvent -> {
            iconResId = R.drawable.ic_book_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is HeadRefRestoredEvent -> {
            iconResId = R.drawable.ic_restore_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is LabeledEvent -> {
            iconResId = R.drawable.ic_label_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is LockedEvent -> {
            iconResId = R.drawable.ic_lock_outline_24dp
            bgResId = R.drawable.bg_issue_timeline_event_2
        }
        is MarkedAsDuplicateEvent -> {
            iconResId = R.drawable.ic_copy_24dp
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is MergedEvent -> {
            iconResId = R.drawable.ic_pr_merged
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is MilestonedEvent -> {
            iconResId = R.drawable.ic_milestone_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is MovedColumnsInProjectEvent -> {
            iconResId = R.drawable.ic_dashboard_outline
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PinnedEvent -> {
            iconResId = R.drawable.ic_pin_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestCommit -> {
            iconResId = R.drawable.ic_commit_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestReview -> {
            when (data.state) {
                PullRequestReviewState.APPROVED -> {
                    iconResId = R.drawable.ic_check_24
                    bgResId = R.drawable.bg_issue_timeline_event_3
                }
                PullRequestReviewState.CHANGES_REQUESTED -> {
                    iconResId = R.drawable.ic_close_24
                    bgResId = R.drawable.bg_issue_timeline_event_2
                }
                // PENDING, DISMISSED, COMMENTED
                else -> {
                    iconResId = R.drawable.ic_eye_24
                    bgResId = R.drawable.bg_issue_timeline_event_1
                }
            }
        }
        is ReadyForReviewEvent -> {
            iconResId = R.drawable.ic_eye_24
            bgResId = R.drawable.bg_issue_timeline_event_3
        }
        is ReferencedEvent -> {
            iconResId = R.drawable.ic_bookmark_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is RemovedFromProjectEvent -> {
            iconResId = R.drawable.ic_dashboard_outline
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is RenamedTitleEvent -> {
            iconResId = R.drawable.ic_edit_white_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is ReopenedEvent -> {
            iconResId = R.drawable.ic_dot_24
            bgResId = R.drawable.bg_issue_timeline_event_3
        }
        is ReviewDismissedEvent -> {
            iconResId = R.drawable.ic_close_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is ReviewRequestRemovedEvent -> {
            iconResId = R.drawable.ic_close_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is ReviewRequestedEvent -> {
            iconResId = R.drawable.ic_eye_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is UnassignedEvent -> {
            iconResId = R.drawable.ic_person_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is UnlabeledEvent -> {
            iconResId = R.drawable.ic_label_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is UnlockedEvent -> {
            iconResId = R.drawable.ic_key_24
            bgResId = R.drawable.bg_issue_timeline_event_2
        }
        is UnpinnedEvent -> {
            iconResId = R.drawable.ic_pin_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        else -> {
            iconResId = null
            bgResId = null
        }
    }

    imageResId(iconResId ?: 0)
    backgroundResId(bgResId ?: 0)
}

@BindingAdapter("prTimelineCommentAuthorAssociation")
fun AppCompatTextView.prTimelineCommentAuthorAssociation(
    data: PullRequestTimelineItem?
) {
    val authorAssociation = when (data) {
        is IssueComment -> {
            data.authorAssociation
        }
        is PullRequestReview -> {
            data.authorAssociation
        }
        else -> {
            null
        }
    }

    authorAssociation(authorAssociation)
}