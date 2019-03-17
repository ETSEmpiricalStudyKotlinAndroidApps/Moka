package io.github.tonnyl.moka.ui.issue

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.LockReason
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.databinding.ItemIssueTimelineCommentBinding
import io.github.tonnyl.moka.databinding.ItemIssueTimelineEventBinding

class IssueTimelineAdapter : PagedListAdapter<IssueTimelineItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<IssueTimelineItem>() {

            override fun areItemsTheSame(oldItem: IssueTimelineItem, newItem: IssueTimelineItem): Boolean = oldItem.hashCode() == newItem.hashCode()

            override fun areContentsTheSame(oldItem: IssueTimelineItem, newItem: IssueTimelineItem): Boolean = oldItem.compare(newItem)

        }

        const val VIEW_TYPE_COMMENT = 0x00
        const val VIEW_TYPE_EVENT = 0x01

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == VIEW_TYPE_COMMENT) {
            CommentViewHolder(ItemIssueTimelineCommentBinding.inflate(inflater, parent, false))
        } else {
            EventViewHolder(ItemIssueTimelineEventBinding.inflate(inflater, parent, false))
        }
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

    class CommentViewHolder(
            private val binding: ItemIssueTimelineCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: IssueCommentEvent) {
            binding.apply {
                avatar = data.authorAvatarUrl?.toString() ?: ""
                login = data.authorLogin ?: ""
                createdAt = data.createdAt
                authorAssociation = data.authorAssociation
                content = data.body
            }

            binding.executePendingBindings()
        }

    }

    class EventViewHolder(
            private val binding: ItemIssueTimelineEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: IssueTimelineItem) {
            val context = binding.root.context

            binding.apply {
                when (data) {
                    is AssignedEvent -> {
                        avatar = data.actorAvatarUrl?.toString()
                        login = data.actorLogin ?: ""
                        createdAt = data.createdAt
                        content = if (data.assigneeLogin == data.assigneeLogin) {
                            context.getString(R.string.issue_timeline_assigned_event_self_assigned)
                        } else {
                            HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_assigned_event_assigned_someone, data.assigneeLogin), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }
                        iconResId = R.drawable.ic_person_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is ClosedEvent -> {
                        avatar = data.avatarUrl?.toString()
                        login = data.login ?: ""
                        createdAt = data.createdAt
                        content = context.getString(R.string.issue_timeline_closed_event_closed)
                        iconResId = R.drawable.ic_pr_issue_close_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_2
                    }
                    is CommitEvent -> {
                        avatar = data.authorAvatarUrl?.toString()
                                ?: data.committerAvatarUri?.toString()
                        login = data.authorLogin ?: data.committerLogin ?: ""
                        createdAt = null
                        content = data.message
                        iconResId = R.drawable.ic_commit_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is CrossReferencedEvent -> {
                        avatar = data.avatarUrl?.toString()
                        login = data.login ?: ""
                        createdAt = data.createdAt
                        content = HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_cross_referenced_event_cross_referenced, data.issue?.title
                                ?: data.pullRequest?.title, data.issue?.number
                                ?: data.pullRequest?.number), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_bookmark_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is DemilestonedEvent -> {
                        avatar = data.avatarUrl?.toString()
                        login = data.login ?: ""
                        createdAt = data.createdAt
                        content = HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_demilestoned_event_demilestoned, data.milestoneTitle), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_milestone_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is LabeledEvent -> {
                        avatar = data.avatarUrl?.toString()
                        login = data.login ?: ""
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
                    is LockedEvent -> {
                        avatar = data.avatarUrl?.toString()
                        login = data.login ?: ""
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
                    is MilestonedEvent -> {
                        avatar = data.avatarUrl?.toString()
                        login = data.login ?: ""
                        createdAt = data.createdAt
                        content = HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_milestoned_event_milestoned, data.milestoneTitle), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_milestone_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is ReferencedEvent -> {
                        avatar = data.avatarUrl?.toString()
                        login = data.login ?: ""
                        createdAt = data.createdAt
                        content = HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_referenced_event_referenced, data.issue?.title
                                ?: data.pullRequest?.title, data.issue?.number
                                ?: data.pullRequest?.number), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_bookmark_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is RenamedTitleEvent -> {
                        avatar = data.avatarUrl?.toString()
                        login = data.login ?: ""
                        createdAt = data.createdAt
                        content = HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_renamed_title_event_change_title, data.previousTitle, data.currentTitle), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_edit_white_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is ReopenedEvent -> {
                        avatar = data.avatarUrl?.toString()
                        login = data.login ?: ""
                        createdAt = data.createdAt
                        content = context.getString(R.string.issue_timeline_reopened_event_reopened)
                        iconResId = R.drawable.ic_dot_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_3
                    }
                    is TransferredEvent -> {
                        avatar = data.avatarUrl?.toString()
                        login = data.login ?: ""
                        createdAt = data.createdAt
                        content = context.getString(R.string.issue_timeline_transferred_event_transferred, data.nameWithOwnerOfFromRepository)
                        iconResId = R.drawable.ic_dot_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is UnassignedEvent -> {
                        avatar = data.actorAvatarUrl?.toString()
                        login = data.actorLogin ?: ""
                        createdAt = data.createdAt
                        content = if (data.assigneeLogin == data.assigneeLogin) context.getString(R.string.issue_timeline_unassigned_event_self_unassigned) else HtmlCompat.fromHtml(context.getString(R.string.issue_timeline_assigned_event_assigned_someone, data.assigneeLogin), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        iconResId = R.drawable.ic_person_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_1
                    }
                    is UnlabeledEvent -> {
                        avatar = data.avatarUrl?.toString()
                        login = data.login ?: ""
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
                    is UnlockedEvent -> {
                        avatar = data.avatarUrl?.toString()
                        login = data.login ?: ""
                        createdAt = data.createdAt
                        content = context.getString(R.string.issue_timeline_unlocked_event_unlocked)
                        iconResId = R.drawable.ic_key_24
                        backgroundResId = R.drawable.bg_issue_timeline_event_2
                    }
                }

            }

            binding.executePendingBindings()
        }

    }

}