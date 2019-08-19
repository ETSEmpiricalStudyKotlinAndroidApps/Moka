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
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.LockReason
import io.github.tonnyl.moka.data.PullRequestReviewState
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.util.*

@BindingAdapter("prTimelineItemContentTextFuture")
fun AppCompatTextView.prTimelineItemContentTextFuture(
    data: PullRequestTimelineItem?
) {
    val content = when (data) {
        is PullRequestAssignedEvent -> {
            if (data.actorLogin == data.assigneeLogin) {
                context.getString(R.string.issue_timeline_assigned_event_self_assigned)
            } else {
                HtmlCompat.fromHtml(
                    context.getString(
                        R.string.issue_timeline_assigned_event_assigned_someone,
                        data.assigneeLogin
                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }
        is PullRequestBaseRefForcePushedEvent -> {
            context.getString(
                R.string.pull_request_force_pushed_branch,
                data.refName,
                data.beforeCommitOid.toShortOid(),
                data.afterCommitOid.toShortOid()
            )
        }
        is PullRequestClosedEvent -> {
            context.getString(R.string.issue_timeline_closed_event_closed)
        }
        is PullRequestCommitEvent -> {
            context.getString(
                R.string.pull_request_review_commit_message_oid,
                data.message,
                data.oid.toShortOid()
            )
        }
        is PullRequestCrossReferencedEvent -> {
            HtmlCompat.fromHtml(
                context.getString(
                    R.string.issue_timeline_cross_referenced_event_cross_referenced,
                    data.issue?.title ?: data.pullRequest?.title,
                    data.issue?.number ?: data.pullRequest?.number
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        is PullRequestDemilestonedEvent -> {
            HtmlCompat.fromHtml(
                context.getString(
                    R.string.issue_timeline_demilestoned_event_demilestoned, data.milestoneTitle
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        is PullRequestDeployedEvent -> {
            HtmlCompat.fromHtml(
                context.getString(
                    R.string.pull_request_deployed_to_branch, data.deploymentEnvironment
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        is PullRequestDeploymentEnvironmentChangedEvent -> {
            HtmlCompat.fromHtml(
                context.getString(
                    R.string.pull_request_changed_deploy_environment, data.environment
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        is PullRequestHeadRefDeletedEvent -> {
            context.getString(
                R.string.pull_request_deleted_branch, data.headRefName
            )
        }
        is PullRequestHeadRefForcePushedEvent -> {
            context.getString(
                R.string.pull_request_force_pushed_branch,
                data.refName,
                data.beforeCommitOid.toShortOid(),
                data.afterCommitOid.toShortOid()
            )
        }
        is PullRequestHeadRefRestoredEvent -> {
            context.getString(R.string.pull_request_restore_branch, data.headRefName)
        }
        is PullRequestLockedEvent -> {
            HtmlCompat.fromHtml(
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
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        is PullRequestLabeledEvent -> {
            val first = context.getString(R.string.issue_timeline_labeled_event_labeled)
            val second = context.getString(R.string.issue_timeline_label)
            val all = "$first ${data.labelName} $second"
            val spannable = SpannableString(all)
            spannable.setSpan(
                BackgroundColorSpan(Color.parseColor("#${data.labelColor}")),
                first.length,
                first.length + data.labelName.length + 2,
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
                first.length + data.labelName.length + 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable
        }
        is PullRequestMergedEvent -> {
            HtmlCompat.fromHtml(
                context.getString(
                    R.string.pull_request_merged_commit,
                    data.commitOid.toShortOid(),
                    data.mergeRefName
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        is PullRequestMilestonedEvent -> {
            HtmlCompat.fromHtml(
                context.getString(
                    R.string.issue_timeline_milestoned_event_milestoned,
                    data.milestoneTitle
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
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
                reviewStateText += HtmlCompat.fromHtml(
                    context.getString(
                        R.string.pull_request_and_left_a_comment,
                        data.body
                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }

            reviewStateText
        }
        is PullRequestReferencedEvent -> {
            HtmlCompat.fromHtml(
                context.getString(
                    R.string.issue_timeline_referenced_event_referenced,
                    data.issue?.title ?: data.pullRequest?.title,
                    data.issue?.number ?: data.pullRequest?.number
                ), HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        is PullRequestRenamedTitleEvent -> {
            HtmlCompat.fromHtml(
                context.getString(
                    R.string.issue_timeline_renamed_title_event_change_title,
                    data.previousTitle,
                    data.currentTitle
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        is PullRequestReopenedEvent -> {
            context.getString(R.string.issue_timeline_reopened_event_reopened)
        }
        is PullRequestReviewDismissedEvent -> {
            if (data.dismissalMessage.isNullOrEmpty()) {
                HtmlCompat.fromHtml(
                    context.getString(
                        R.string.pull_request_dismiss_someones_review_and_left_a_comment,
                        data.reviewAuthorLogin,
                        data.dismissalMessage
                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            } else {
                HtmlCompat.fromHtml(
                    context.getString(
                        R.string.pull_request_dismiss_someones_review, data.reviewAuthorLogin
                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }
        is PullRequestReviewRequestRemovedEvent -> {
            HtmlCompat.fromHtml(
                context.getString(
                    R.string.pull_request_removed_someones_review_request,
                    data.requestedReviewerUserLogin ?: data.requestedReviewerTeamCombinedSlug
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        is PullRequestReviewRequestedEvent -> {
            if (data.login == data.requestedReviewerUserLogin
                || data.login == data.requestedReviewerTeamCombinedSlug
            ) {
                context.getString(R.string.pull_request_self_requested_a_review)
            } else {
                HtmlCompat.fromHtml(
                    context.getString(
                        R.string.pull_request_requested_review_from,
                        data.requestedReviewerUserLogin ?: data.requestedReviewerTeamCombinedSlug
                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }
        is PullRequestUnassignedEvent -> {
            if (data.actorLogin == data.assigneeLogin) {
                context.getString(R.string.issue_timeline_unassigned_event_self_unassigned)
            } else {
                HtmlCompat.fromHtml(
                    context.getString(
                        R.string.issue_timeline_unassigned_event_unassigned_someone,
                        data.assigneeLogin
                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }
        is PullRequestUnlabeledEvent -> {
            val first = context.getString(R.string.issue_timeline_unlabeled_event_unlabeled)
            val second = context.getString(R.string.issue_timeline_label)
            val all = "$first ${data.labelName} $second"
            val spannable = SpannableString(all)
            spannable.setSpan(
                BackgroundColorSpan(Color.parseColor("#${data.labelColor}")),
                first.length,
                first.length + data.labelName.length + 2,
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
                first.length + data.labelName.length + 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable
        }
        is PullRequestUnlockedEvent -> {
            context.getString(R.string.issue_timeline_unlocked_event_unlocked)
        }
        is PullRequestIssueComment -> {
            data.body
        }
        is PullRequestReviewComment -> {
            data.body
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
        is PullRequestAssignedEvent -> {
            data.actorLogin
        }
        is PullRequestBaseRefForcePushedEvent -> {
            data.actorLogin
        }
        is PullRequestClosedEvent -> {
            data.login
        }
        is PullRequestCommitEvent -> {
            data.authorLogin ?: data.committerLogin
        }
        is PullRequestCrossReferencedEvent -> {
            data.login
        }
        is PullRequestDemilestonedEvent -> {
            data.login
        }
        is PullRequestDeployedEvent -> {
            data.login
        }
        is PullRequestDeploymentEnvironmentChangedEvent -> {
            data.login
        }
        is PullRequestHeadRefDeletedEvent -> {
            data.login
        }
        is PullRequestHeadRefForcePushedEvent -> {
            data.login
        }
        is PullRequestHeadRefRestoredEvent -> {
            data.login
        }
        is PullRequestLockedEvent -> {
            data.login
        }
        is PullRequestLabeledEvent -> {
            data.login
        }
        is PullRequestMergedEvent -> {
            data.login
        }
        is PullRequestMilestonedEvent -> {
            data.login
        }
        is PullRequestReview -> {
            data.login
        }
        is PullRequestReferencedEvent -> {
            data.login
        }
        is PullRequestRenamedTitleEvent -> {
            data.login
        }
        is PullRequestReopenedEvent -> {
            data.login
        }
        is PullRequestReviewDismissedEvent -> {
            data.login
        }
        is PullRequestReviewRequestRemovedEvent -> {
            data.login
        }
        is PullRequestReviewRequestedEvent -> {
            data.login
        }
        is PullRequestUnassignedEvent -> {
            data.actorLogin
        }
        is PullRequestUnlabeledEvent -> {
            data.login
        }
        is PullRequestUnlockedEvent -> {
            data.login
        }
        is PullRequestIssueComment -> {
            data.authorLogin
        }
        is PullRequestReviewComment -> {
            data.authorLogin
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
        is PullRequestAssignedEvent -> {
            data.actorAvatarUrl?.toString()
        }
        is PullRequestBaseRefForcePushedEvent -> {
            data.actorAvatarUrl?.toString()
        }
        is PullRequestClosedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestCommitEvent -> {
            data.authorAvatarUrl?.toString() ?: data.committerAvatarUrl?.toString()
        }
        is PullRequestCrossReferencedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestDemilestonedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestDeployedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestDeploymentEnvironmentChangedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestHeadRefDeletedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestHeadRefForcePushedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestHeadRefRestoredEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestLockedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestLabeledEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestMergedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestMilestonedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestReview -> {
            data.avatarUrl?.toString()
        }
        is PullRequestReferencedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestRenamedTitleEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestReopenedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestReviewDismissedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestReviewRequestRemovedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestReviewRequestedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestUnassignedEvent -> {
            data.actorAvatarUrl?.toString()
        }
        is PullRequestUnlabeledEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestUnlockedEvent -> {
            data.avatarUrl?.toString()
        }
        is PullRequestIssueComment -> {
            data.authorAvatarUrl?.toString()
        }
        is PullRequestReviewComment -> {
            data.authorAvatarUrl?.toString()
        }
        else -> {
            null
        }
    }

    avatarUrl(avatarUrl ?: "")
}

@BindingAdapter("prTimelineItemCreatedAt")
fun AppCompatTextView.prTimelineItemCreatedAt(
    data: PullRequestTimelineItem?
) {
    val createdAt = when (data) {
        is PullRequestAssignedEvent -> {
            data.createdAt
        }
        is PullRequestBaseRefForcePushedEvent -> {
            data.createdAt
        }
        is PullRequestClosedEvent -> {
            data.createdAt
        }
        is PullRequestCommitEvent -> {
            null
        }
        is PullRequestCrossReferencedEvent -> {
            data.createdAt
        }
        is PullRequestDemilestonedEvent -> {
            data.createdAt
        }
        is PullRequestDeployedEvent -> {
            data.createdAt
        }
        is PullRequestDeploymentEnvironmentChangedEvent -> {
            data.createdAt
        }
        is PullRequestHeadRefDeletedEvent -> {
            data.createdAt
        }
        is PullRequestHeadRefForcePushedEvent -> {
            data.createdAt
        }
        is PullRequestHeadRefRestoredEvent -> {
            data.createdAt
        }
        is PullRequestLockedEvent -> {
            data.createdAt
        }
        is PullRequestLabeledEvent -> {
            data.createdAt
        }
        is PullRequestMergedEvent -> {
            data.createdAt
        }
        is PullRequestMilestonedEvent -> {
            data.createdAt
        }
        is PullRequestReview -> {
            data.createdAt
        }
        is PullRequestReferencedEvent -> {
            data.createdAt
        }
        is PullRequestRenamedTitleEvent -> {
            data.createdAt
        }
        is PullRequestReopenedEvent -> {
            data.createdAt
        }
        is PullRequestReviewDismissedEvent -> {
            data.createdAt
        }
        is PullRequestReviewRequestRemovedEvent -> {
            data.createdAt
        }
        is PullRequestReviewRequestedEvent -> {
            data.createdAt
        }
        is PullRequestUnassignedEvent -> {
            data.createdAt
        }
        is PullRequestUnlabeledEvent -> {
            data.createdAt
        }
        is PullRequestUnlockedEvent -> {
            data.createdAt
        }
        is PullRequestIssueComment -> {
            data.createdAt
        }
        is PullRequestReviewComment -> {
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
        is PullRequestAssignedEvent -> {
            iconResId = R.drawable.ic_person_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestBaseRefForcePushedEvent -> {
            iconResId = R.drawable.ic_person_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestClosedEvent -> {
            iconResId = R.drawable.ic_pr_issue_close_24
            bgResId = R.drawable.bg_issue_timeline_event_2
        }
        is PullRequestCommitEvent -> {
            iconResId = R.drawable.ic_commit_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestCrossReferencedEvent -> {
            iconResId = R.drawable.ic_bookmark_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestDemilestonedEvent -> {
            iconResId = R.drawable.ic_milestone_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestDeployedEvent -> {
            iconResId = R.drawable.ic_rocket_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestDeploymentEnvironmentChangedEvent -> {
            iconResId = R.drawable.ic_rocket_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestHeadRefDeletedEvent -> {
            iconResId = R.drawable.ic_delete_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestHeadRefForcePushedEvent -> {
            iconResId = R.drawable.ic_person_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestHeadRefRestoredEvent -> {
            iconResId = R.drawable.ic_restore_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestLockedEvent -> {
            iconResId = R.drawable.ic_lock_outline_24dp
            bgResId = R.drawable.bg_issue_timeline_event_2
        }
        is PullRequestLabeledEvent -> {
            iconResId = R.drawable.ic_label_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestMergedEvent -> {
            iconResId = R.drawable.ic_pr_merged
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestMilestonedEvent -> {
            iconResId = R.drawable.ic_milestone_24
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
        is PullRequestReferencedEvent -> {
            iconResId = R.drawable.ic_bookmark_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestRenamedTitleEvent -> {
            iconResId = R.drawable.ic_edit_white_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestReopenedEvent -> {
            iconResId = R.drawable.ic_dot_24
            bgResId = R.drawable.bg_issue_timeline_event_3
        }
        is PullRequestReviewDismissedEvent -> {
            iconResId = R.drawable.ic_close_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestReviewRequestRemovedEvent -> {
            iconResId = R.drawable.ic_close_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestReviewRequestedEvent -> {
            iconResId = R.drawable.ic_eye_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestUnassignedEvent -> {
            iconResId = R.drawable.ic_person_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestUnlabeledEvent -> {
            iconResId = R.drawable.ic_label_24
            bgResId = R.drawable.bg_issue_timeline_event_1
        }
        is PullRequestUnlockedEvent -> {
            iconResId = R.drawable.ic_key_24
            bgResId = R.drawable.bg_issue_timeline_event_2
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
        is PullRequestIssueComment -> {
            data.authorAssociation
        }
        is PullRequestReviewComment -> {
            data.authorAssociation
        }
        else -> {
            null
        }
    }

    authorAssociation(authorAssociation)
}