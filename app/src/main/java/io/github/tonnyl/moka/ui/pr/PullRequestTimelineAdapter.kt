package io.github.tonnyl.moka.ui.pr

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateUtils
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.net.GlideLoader
import io.github.tonnyl.moka.type.CommentAuthorAssociation
import io.github.tonnyl.moka.type.LockReason
import io.github.tonnyl.moka.type.PullRequestReviewState
import io.github.tonnyl.moka.util.toShortOid
import kotlinx.android.synthetic.main.item_issue_timeline_comment.view.*
import kotlinx.android.synthetic.main.item_issue_timeline_event.view.*

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        VIEW_TYPE_COMMENT -> CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_issue_timeline_comment, parent, false))
        VIEW_TYPE_THREAD -> ThreadViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_issue_timeline_thread, parent, false))
        else -> EventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_issue_timeline_event, parent, false))
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

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindTo(data: PullRequestTimelineItem?) {
            if (data == null) {
                return
            }

            with(itemView) {
                when (data) {
                    is PullRequestIssueComment -> {
                        GlideLoader.loadAvatar(data.authorAvatarUrl?.toString(), issue_timeline_comment_avatar)

                        issue_timeline_comment_username.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.authorLogin ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_comment_username),
                                null
                        ))

                        issue_timeline_comment_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_comment_created_at),
                                null
                        ))

                        issue_timeline_comment_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.body,
                                TextViewCompat.getTextMetricsParams(issue_timeline_comment_content),
                                null
                        ))

                        val stringResId = when (data.authorAssociation) {
                            CommentAuthorAssociation.COLLABORATOR -> R.string.author_association_collaborator
                            CommentAuthorAssociation.CONTRIBUTOR -> R.string.author_association_contributor
                            CommentAuthorAssociation.FIRST_TIMER -> R.string.author_association_first_timer
                            CommentAuthorAssociation.FIRST_TIME_CONTRIBUTOR -> R.string.author_association_first_timer_contributor
                            CommentAuthorAssociation.MEMBER -> R.string.author_association_member
                            CommentAuthorAssociation.OWNER -> R.string.author_association_owner
                            else -> -1
                        }
                        issue_timeline_comment_author_association.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                if (stringResId != -1) context.getString(stringResId) else "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_comment_author_association),
                                null
                        ))
                    }
                    is PullRequestReviewComment -> {
                        GlideLoader.loadAvatar(data.authorAvatarUrl?.toString(), issue_timeline_comment_avatar)

                        issue_timeline_comment_username.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.authorLogin ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_comment_username),
                                null
                        ))

                        issue_timeline_comment_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_comment_created_at),
                                null
                        ))

                        issue_timeline_comment_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.body,
                                TextViewCompat.getTextMetricsParams(issue_timeline_comment_content),
                                null
                        ))

                        val stringResId = when (data.authorAssociation) {
                            CommentAuthorAssociation.COLLABORATOR -> R.string.author_association_collaborator
                            CommentAuthorAssociation.CONTRIBUTOR -> R.string.author_association_contributor
                            CommentAuthorAssociation.FIRST_TIMER -> R.string.author_association_first_timer
                            CommentAuthorAssociation.FIRST_TIME_CONTRIBUTOR -> R.string.author_association_first_timer_contributor
                            CommentAuthorAssociation.MEMBER -> R.string.author_association_member
                            CommentAuthorAssociation.OWNER -> R.string.author_association_owner
                            else -> -1
                        }
                        issue_timeline_comment_author_association.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                if (stringResId != -1) context.getString(stringResId) else "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_comment_author_association),
                                null
                        ))
                    }
                }
            }
        }

    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindTo(data: PullRequestTimelineItem?) {
            if (data == null) {
                return
            }

            with(itemView) {
                when (data) {
                    is PullRequestAssignedEvent -> {
                        GlideLoader.loadAvatar(data.actorAvatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_person_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.actorLogin ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))
                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))
                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                if (data.actorLogin == data.assigneeLogin) context.getString(R.string.issue_timeline_assigned_event_self_assigned) else HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_assigned_event_assigned_someone, data.assigneeLogin), HtmlCompat.FROM_HTML_MODE_LEGACY),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestBaseRefForcePushedEvent -> {
                        GlideLoader.loadAvatar(data.actorAvatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_person_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.actorLogin ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))
                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))
                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                context.getString(R.string.pull_request_force_pushed_branch, data.refName, data.beforeCommitOid.toShortOid(), data.afterCommitOid.toShortOid()),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestClosedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_pr_issue_close_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_2)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                context.getString(R.string.issue_timeline_closed_event_closed),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestCommitEvent -> {
                        GlideLoader.loadAvatar(data.authorAvatarUrl?.toString()
                                ?: data.committerAvatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_commit_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.authorLogin ?: data.committerLogin ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                context.getString(R.string.pull_request_review_commit_message_oid, data.message, data.oid.toShortOid()),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestCrossReferencedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_bookmark_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_cross_referenced_event_cross_referenced, data.issue?.title
                                        ?: data.pullRequest?.title, data.issue?.number
                                        ?: data.pullRequest?.number), HtmlCompat.FROM_HTML_MODE_LEGACY),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestDemilestonedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_milestone_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_demilestoned_event_demilestoned, data.milestoneTitle), HtmlCompat.FROM_HTML_MODE_LEGACY),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestDeployedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_rocket_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                HtmlCompat.fromHtml(context.getString(R.string.pull_request_deployed_to_branch, data.deploymentEnvironment), HtmlCompat.FROM_HTML_MODE_LEGACY),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestDeploymentEnvironmentChangedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_rocket_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                HtmlCompat.fromHtml(context.getString(R.string.pull_request_changed_deploy_environment, data.environment), HtmlCompat.FROM_HTML_MODE_LEGACY),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestHeadRefDeletedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_delete_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                context.getString(R.string.pull_request_deleted_branch, data.headRefName),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestHeadRefForcePushedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_person_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))
                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))
                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                context.getString(R.string.pull_request_force_pushed_branch, data.refName, data.beforeCommitOid.toShortOid(), data.afterCommitOid.toShortOid()),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestHeadRefRestoredEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_restore_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                context.getString(R.string.pull_request_restore_branch, data.headRefName),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestLabeledEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_label_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        val first = context.getString(R.string.issue_timeline_labeled_event_labeled)
                        val second = context.getString(R.string.issue_timeline_label)
                        val all = "$first ${data.labelName} $second"
                        val spannable = SpannableString(all)
                        spannable.setSpan(BackgroundColorSpan(Color.parseColor("#${data.labelColor}")), first.length, first.length + data.labelName.length + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        spannable.setSpan(ForegroundColorSpan(ResourcesCompat.getColor(context.resources, R.color.issue_pr_label_text, null)), first.length, first.length + data.labelName.length + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                spannable,
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestLockedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_lock_outline_24dp)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_2)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_locked_event_locked_as,
                                        context.getString(when (data.lockReason) {
                                            LockReason.OFF_TOPIC -> R.string.issue_lock_reason_off_topic
                                            LockReason.RESOLVED -> R.string.issue_lock_reason_resolved
                                            LockReason.SPAM -> R.string.issue_lock_reason_spam
                                            LockReason.TOO_HEATED -> R.string.issue_lock_reason_too_heated
                                            else -> R.string.issue_lock_reason_too_heated
                                        })), HtmlCompat.FROM_HTML_MODE_LEGACY),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestMergedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_pr_merged)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                HtmlCompat.fromHtml(context.getString(R.string.pull_request_merged_commit, data.commitOid.toShortOid(), data.mergeRefName), HtmlCompat.FROM_HTML_MODE_LEGACY),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestMilestonedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_milestone_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_milestoned_event_milestoned, data.milestoneTitle), HtmlCompat.FROM_HTML_MODE_LEGACY),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestReview -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)

                        var reviewStateText: CharSequence
                        @DrawableRes
                        val iconResId: Int
                        @DrawableRes
                        val iconBgResId: Int

                        when (data.state) {
                            PullRequestReviewState.APPROVED -> {
                                iconResId = R.drawable.ic_check_24
                                iconBgResId = R.drawable.bg_issue_timeline_event_3

                                reviewStateText = context.getString(R.string.pull_request_review_approved_changes)
                            }
                            PullRequestReviewState.CHANGES_REQUESTED -> {
                                iconResId = R.drawable.ic_close_24
                                iconBgResId = R.drawable.bg_issue_timeline_event_2

                                reviewStateText = context.getString(R.string.pull_request_review_request_changes)
                            }
                            // PENDING, DISMISSED, COMMENTED
                            else -> {
                                iconResId = R.drawable.ic_eye_24
                                iconBgResId = R.drawable.bg_issue_timeline_event_1

                                reviewStateText = context.getString(R.string.pull_request_reviewed)
                            }
                        }

                        issue_timeline_event_icon.setImageResource(iconResId)
                        issue_timeline_event_icon.setBackgroundResource(iconBgResId)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        if (data.body.isNotEmpty()) {
                            reviewStateText += HtmlCompat.fromHtml(context.getString(R.string.pull_request_and_left_a_comment, data.body), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                reviewStateText,
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestReferencedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_bookmark_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_referenced_event_referenced, data.issue?.title
                                        ?: data.pullRequest?.title, data.issue?.number
                                        ?: data.pullRequest?.number), HtmlCompat.FROM_HTML_MODE_LEGACY),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestRenamedTitleEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_edit_white_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_renamed_title_event_change_title, data.previousTitle, data.currentTitle), HtmlCompat.FROM_HTML_MODE_LEGACY),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestReopenedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_dot_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_3)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                context.getString(R.string.issue_timeline_reopened_event_reopened),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestReviewDismissedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_close_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        val body = if (data.message.isNotEmpty()) HtmlCompat.fromHtml(context.getString(R.string.pull_request_dismiss_someones_review_and_left_a_comment, data.reviewAuthorLogin, data.message), HtmlCompat.FROM_HTML_MODE_LEGACY) else HtmlCompat.fromHtml(context.getString(R.string.pull_request_dismiss_someones_review, data.reviewAuthorLogin), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                body,
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestReviewRequestRemovedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_close_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                HtmlCompat.fromHtml(context.getString(R.string.pull_request_removed_someones_review_request, data.requestedReviewerUserLogin
                                        ?: data.requestedReviewerTeamCombinedSlug), HtmlCompat.FROM_HTML_MODE_LEGACY),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestReviewRequestedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_eye_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        val contentText = if (data.login == data.requestedReviewerUserLogin || data.login == data.requestedReviewerTeamCombinedSlug) {
                            context.getString(R.string.pull_request_self_requested_a_review)
                        } else {
                            HtmlCompat.fromHtml(context.getString(R.string.pull_request_requested_review_from, data.requestedReviewerUserLogin
                                    ?: data.requestedReviewerTeamCombinedSlug), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }
                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                contentText,
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestUnassignedEvent -> {
                        GlideLoader.loadAvatar(data.actorAvatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_person_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.actorLogin ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                if (data.actorLogin == data.assigneeLogin) context.getString(R.string.issue_timeline_unassigned_event_self_unassigned) else HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_unassigned_event_unassigned_someone, data.assigneeLogin), HtmlCompat.FROM_HTML_MODE_LEGACY),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestUnlabeledEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_label_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_1)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        val first = context.getString(R.string.issue_timeline_unlabeled_event_unlabeled)
                        val second = context.getString(R.string.issue_timeline_label)
                        val all = "$first ${data.labelName} $second"
                        val spannable = SpannableString(all)
                        spannable.setSpan(BackgroundColorSpan(Color.parseColor("#${data.labelColor}")), first.length, first.length + data.labelName.length + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        spannable.setSpan(ForegroundColorSpan(ResourcesCompat.getColor(context.resources, R.color.issue_pr_label_text, null)), first.length, first.length + data.labelName.length + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                spannable,
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is PullRequestUnlockedEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_key_24)
                        issue_timeline_event_icon.setBackgroundResource(R.drawable.bg_issue_timeline_event_2)

                        issue_timeline_event_author_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                data.login ?: "",
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_author_name),
                                null
                        ))

                        issue_timeline_event_created_at.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_created_at),
                                null
                        ))

                        issue_timeline_event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                                context.getString(R.string.issue_timeline_unlocked_event_unlocked),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                }
            }
        }

    }

    class ThreadViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindTo(data: PullRequestTimelineItem?) {
            if (data == null) {
                return
            }

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