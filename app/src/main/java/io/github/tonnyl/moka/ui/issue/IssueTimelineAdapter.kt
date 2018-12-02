package io.github.tonnyl.moka.ui.issue

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateUtils
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.CommentAuthorAssociation
import io.github.tonnyl.moka.data.LockReason
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.net.GlideLoader
import kotlinx.android.synthetic.main.item_issue_timeline_comment.view.*
import kotlinx.android.synthetic.main.item_issue_timeline_event.view.*

class IssueTimelineAdapter : PagedListAdapter<IssueTimelineItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<IssueTimelineItem>() {

            override fun areItemsTheSame(oldItem: IssueTimelineItem, newItem: IssueTimelineItem): Boolean = oldItem.hashCode() == newItem.hashCode()

            override fun areContentsTheSame(oldItem: IssueTimelineItem, newItem: IssueTimelineItem): Boolean = oldItem.compare(newItem)

        }

        const val VIEW_TYPE_COMMENT = 0x00
        const val VIEW_TYPE_EVENT = 0x01

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = if (viewType == VIEW_TYPE_COMMENT) {
        CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_issue_timeline_comment, parent, false))
    } else {
        EventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_issue_timeline_event, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (getItemViewType(position) == VIEW_TYPE_COMMENT) {
            (holder as CommentViewHolder).bindTo(item as IssueCommentEvent)
        } else {
            (holder as EventViewHolder).bindTo(item)
        }
    }

    override fun getItemViewType(position: Int): Int = if (getItem(position) is IssueCommentEvent) VIEW_TYPE_COMMENT else VIEW_TYPE_EVENT

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindTo(data: IssueCommentEvent?) {
            if (data == null) {
                return
            }

            with(itemView) {
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
                    CommentAuthorAssociation.NONE -> -1
                    CommentAuthorAssociation.OWNER -> R.string.author_association_owner
                }
                issue_timeline_comment_author_association.setTextFuture(PrecomputedTextCompat.getTextFuture(
                        if (stringResId != -1) context.getString(stringResId) else "",
                        TextViewCompat.getTextMetricsParams(issue_timeline_comment_author_association),
                        null
                ))
            }
        }

    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindTo(data: IssueTimelineItem) {
            with(itemView) {
                when (data) {
                    is AssignedEvent -> {
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
                                if (data.assigneeLogin == data.assigneeLogin) context.getString(R.string.issue_timeline_assigned_event_self_assigned) else HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_assigned_event_assigned_someone, data.assigneeLogin), HtmlCompat.FROM_HTML_MODE_LEGACY),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is ClosedEvent -> {
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
                    is CommitEvent -> {
                        GlideLoader.loadAvatar(data.authorAvatarUrl?.toString()
                                ?: data.committerAvatarUri?.toString(), issue_timeline_event_author_avatar)
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
                                data.message,
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is CrossReferencedEvent -> {
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
                    is DemilestonedEvent -> {
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
                    is LabeledEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_tag_24)
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
                    is LockedEvent -> {
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
                    is MilestonedEvent -> {
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
                    is ReferencedEvent -> {
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
                    is RenamedTitleEvent -> {
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
                    is ReopenedEvent -> {
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
                    is TransferredEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_dot_24)
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
                                context.getString(R.string.issue_timeline_transferred_event_transferred, data.nameWithOwnerOfFromRepository),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is UnassignedEvent -> {
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
                                if (data.assigneeLogin == data.assigneeLogin) context.getString(R.string.issue_timeline_unassigned_event_self_unassigned) else HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_assigned_event_assigned_someone, data.assigneeLogin), HtmlCompat.FROM_HTML_MODE_LEGACY),
                                TextViewCompat.getTextMetricsParams(issue_timeline_event_content),
                                null
                        ))
                    }
                    is UnlabeledEvent -> {
                        GlideLoader.loadAvatar(data.avatarUrl?.toString(), issue_timeline_event_author_avatar)
                        issue_timeline_event_icon.setImageResource(R.drawable.ic_tag_24)
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
                    is UnlockedEvent -> {
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

}