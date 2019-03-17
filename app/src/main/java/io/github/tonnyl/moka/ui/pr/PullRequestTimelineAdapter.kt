package io.github.tonnyl.moka.ui.pr

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.LockReason
import io.github.tonnyl.moka.data.PullRequestReviewState
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.databinding.ItemIssueTimelineCommentBinding
import io.github.tonnyl.moka.databinding.ItemIssueTimelineEventBinding
import io.github.tonnyl.moka.util.toShortOid

class PullRequestTimelineAdapter : PagedListAdapter<PullRequestTimelineItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PullRequestTimelineItem>() {

            override fun areItemsTheSame(oldItem: PullRequestTimelineItem, newItem: PullRequestTimelineItem): Boolean = oldItem.hashCode() == newItem.hashCode()

            override fun areContentsTheSame(oldItem: PullRequestTimelineItem, newItem: PullRequestTimelineItem): Boolean = oldItem.compare(newItem)

        }

        const val VIEW_TYPE_COMMENT = 0x00
        const val VIEW_TYPE_EVENT = 0x01
        const val VIEW_TYPE_THREAD = 0x02

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_COMMENT -> CommentViewHolder(ItemIssueTimelineCommentBinding.inflate(inflater, parent, false))
            VIEW_TYPE_THREAD -> ThreadViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_issue_timeline_thread, parent, false))
            else -> EventViewHolder(ItemIssueTimelineEventBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val viewType = getItemViewType(position)

        when (viewType) {
            VIEW_TYPE_COMMENT -> {
                (holder as CommentViewHolder).bindTo(item)
            }
            VIEW_TYPE_THREAD -> {
                (holder as ThreadViewHolder).bindTo(item)
            }
            else -> {
                (holder as EventViewHolder).bindTo(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is PullRequestIssueComment, is PullRequestReviewComment -> VIEW_TYPE_COMMENT
        is PullRequestReviewThread, is PullRequestCommitCommentThread -> VIEW_TYPE_THREAD
        else -> VIEW_TYPE_EVENT
    }

    class CommentViewHolder(
            private val binding: ItemIssueTimelineCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: PullRequestTimelineItem) {
            binding.apply {
                when (data) {
                    is PullRequestIssueComment -> {
                        avatar = data.authorAvatarUrl?.toString()
                        login = data.authorLogin ?: ""
                        createdAt = data.createdAt
                        content = data.body
                        authorAssociation = data.authorAssociation
                    }
                    is PullRequestReviewComment -> {
                        avatar = data.authorAvatarUrl?.toString()
                        login = data.authorLogin ?: ""
                        createdAt = data.createdAt
                        content = data.body
                        authorAssociation = data.authorAssociation
                    }
                }
            }

            binding.executePendingBindings()
        }

    }

    class EventViewHolder(
            private val binding: ItemIssueTimelineEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: PullRequestTimelineItem) {
            val context = binding.root.context

            binding.apply {
                when (data) {
                    is PullRequestAssignedEvent -> {
                        login = data.actorLogin ?: ""
                        avatar = data.actorAvatarUrl?.toString()
                        createdAt = data.createdAt
                        content = if (data.actorLogin == data.assigneeLogin) context.getString(R.string.issue_timeline_assigned_event_self_assigned) else HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_assigned_event_assigned_someone, data.assigneeLogin), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_person_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestBaseRefForcePushedEvent -> {
                        login = data.actorLogin ?: ""
                        avatar = data.actorAvatarUrl?.toString()
                        createdAt = data.createdAt
                        content = context.getString(R.string.pull_request_force_pushed_branch, data.refName, data.beforeCommitOid.toShortOid(), data.afterCommitOid.toShortOid())
                        iconResId = R.drawable.ic_person_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestClosedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = context.getString(R.string.issue_timeline_closed_event_closed)
                        iconResId = R.drawable.ic_pr_issue_close_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_2
                    }
                    is PullRequestCommitEvent -> {
                        login = data.authorLogin ?: data.committerLogin ?: ""
                        avatar = data.authorAvatarUrl?.toString()
                                ?: data.committerAvatarUrl?.toString()
                        createdAt = null
                        content = context.getString(R.string.pull_request_review_commit_message_oid, data.message, data.oid.toShortOid())
                        iconResId = R.drawable.ic_commit_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestCrossReferencedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_cross_referenced_event_cross_referenced, data.issue?.title
                                ?: data.pullRequest?.title, data.issue?.number
                                ?: data.pullRequest?.number), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_bookmark_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestDemilestonedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_demilestoned_event_demilestoned, data.milestoneTitle), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_milestone_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestDeployedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = HtmlCompat.fromHtml(context.getString(R.string.pull_request_deployed_to_branch, data.deploymentEnvironment), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_rocket_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestDeploymentEnvironmentChangedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = HtmlCompat.fromHtml(context.getString(R.string.pull_request_changed_deploy_environment, data.environment), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_rocket_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestHeadRefDeletedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = context.getString(R.string.pull_request_deleted_branch, data.headRefName)
                        iconResId = R.drawable.ic_delete_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestHeadRefForcePushedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = context.getString(R.string.pull_request_force_pushed_branch, data.refName, data.beforeCommitOid.toShortOid(), data.afterCommitOid.toShortOid())
                        iconResId = R.drawable.ic_person_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestHeadRefRestoredEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = context.getString(R.string.pull_request_restore_branch, data.headRefName)
                        iconResId = R.drawable.ic_restore_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestLabeledEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt

                        val first = context.getString(R.string.issue_timeline_labeled_event_labeled)
                        val second = context.getString(R.string.issue_timeline_label)
                        val all = "$first ${data.labelName} $second"
                        val spannable = SpannableString(all)
                        spannable.setSpan(BackgroundColorSpan(Color.parseColor("#${data.labelColor}")), first.length, first.length + data.labelName.length + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        spannable.setSpan(ForegroundColorSpan(ResourcesCompat.getColor(context.resources, R.color.issue_pr_label_text, null)), first.length, first.length + data.labelName.length + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        content = spannable

                        iconResId = R.drawable.ic_label_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestLockedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_locked_event_locked_as,
                                context.getString(when (data.lockReason) {
                                    LockReason.OFF_TOPIC -> R.string.issue_lock_reason_off_topic
                                    LockReason.RESOLVED -> R.string.issue_lock_reason_resolved
                                    LockReason.SPAM -> R.string.issue_lock_reason_spam
                                    LockReason.TOO_HEATED -> R.string.issue_lock_reason_too_heated
                                    else -> R.string.issue_lock_reason_too_heated
                                })), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_lock_outline_24dp
                        backgroundResId = R.drawable.bg_issue_timeline_event_2
                    }
                    is PullRequestMergedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = HtmlCompat.fromHtml(context.getString(R.string.pull_request_merged_commit, data.commitOid.toShortOid(), data.mergeRefName), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_pr_merged
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestMilestonedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_milestoned_event_milestoned, data.milestoneTitle), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_milestone_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestReview -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt

                        var reviewStateText: CharSequence

                        when (data.state) {
                            PullRequestReviewState.APPROVED -> {
                                iconResId = R.drawable.ic_check_24
                                backgroundResId = R.drawable.bg_issue_timeline_event_3

                                reviewStateText = context.getString(R.string.pull_request_review_approved_changes)
                            }
                            PullRequestReviewState.CHANGES_REQUESTED -> {
                                iconResId = R.drawable.ic_close_24
                                backgroundResId = R.drawable.bg_issue_timeline_event_2

                                reviewStateText = context.getString(R.string.pull_request_review_request_changes)
                            }
                            // PENDING, DISMISSED, COMMENTED
                            else -> {
                                iconResId = R.drawable.ic_eye_24
                                backgroundResId = R.drawable.bg_issue_timeline_event_1

                                reviewStateText = context.getString(R.string.pull_request_reviewed)
                            }
                        }

                        if (data.body.isNotEmpty()) {
                            reviewStateText += HtmlCompat.fromHtml(context.getString(R.string.pull_request_and_left_a_comment, data.body), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }

                        content = reviewStateText
                    }
                    is PullRequestReferencedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_referenced_event_referenced, data.issue?.title
                                ?: data.pullRequest?.title, data.issue?.number
                                ?: data.pullRequest?.number), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_bookmark_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestRenamedTitleEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_renamed_title_event_change_title, data.previousTitle, data.currentTitle), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_edit_white_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestReopenedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = context.getString(R.string.issue_timeline_reopened_event_reopened)
                        iconResId = R.drawable.ic_dot_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_3
                    }
                    is PullRequestReviewDismissedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = if (data.message.isNotEmpty()) HtmlCompat.fromHtml(context.getString(R.string.pull_request_dismiss_someones_review_and_left_a_comment, data.reviewAuthorLogin, data.message), HtmlCompat.FROM_HTML_MODE_LEGACY) else HtmlCompat.fromHtml(context.getString(R.string.pull_request_dismiss_someones_review, data.reviewAuthorLogin), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_close_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestReviewRequestRemovedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = HtmlCompat.fromHtml(context.getString(R.string.pull_request_removed_someones_review_request, data.requestedReviewerUserLogin
                                ?: data.requestedReviewerTeamCombinedSlug), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_close_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestReviewRequestedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = if (data.login == data.requestedReviewerUserLogin || data.login == data.requestedReviewerTeamCombinedSlug) {
                            context.getString(R.string.pull_request_self_requested_a_review)
                        } else {
                            HtmlCompat.fromHtml(context.getString(R.string.pull_request_requested_review_from, data.requestedReviewerUserLogin
                                    ?: data.requestedReviewerTeamCombinedSlug), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }
                        iconResId = R.drawable.ic_eye_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestUnassignedEvent -> {
                        login = data.actorLogin ?: ""
                        avatar = data.actorAvatarUrl?.toString()
                        createdAt = data.createdAt
                        content = if (data.actorLogin == data.assigneeLogin) context.getString(R.string.issue_timeline_unassigned_event_self_unassigned) else HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_unassigned_event_unassigned_someone, data.assigneeLogin), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_person_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestUnlabeledEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt

                        val first = context.getString(R.string.issue_timeline_unlabeled_event_unlabeled)
                        val second = context.getString(R.string.issue_timeline_label)
                        val all = "$first ${data.labelName} $second"
                        val spannable = SpannableString(all)
                        spannable.setSpan(BackgroundColorSpan(Color.parseColor("#${data.labelColor}")), first.length, first.length + data.labelName.length + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        spannable.setSpan(ForegroundColorSpan(ResourcesCompat.getColor(context.resources, R.color.issue_pr_label_text, null)), first.length, first.length + data.labelName.length + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        content = spannable

                        iconResId = R.drawable.ic_label_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is PullRequestUnlockedEvent -> {
                        login = data.login ?: ""
                        avatar = data.avatarUrl?.toString()
                        createdAt = data.createdAt
                        content = context.getString(R.string.issue_timeline_unlocked_event_unlocked)
                        iconResId = R.drawable.ic_key_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_2
                    }
                }

                binding.executePendingBindings()
            }
        }
    }

    class ThreadViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindTo(data: PullRequestTimelineItem) {
            with(itemView) {
                when (data) {
                    is PullRequestReviewThread -> {

                    }
                    is PullRequestCommitCommentThread -> {

                    }
                    else -> {

                    }
                }
            }
        }

    }

}