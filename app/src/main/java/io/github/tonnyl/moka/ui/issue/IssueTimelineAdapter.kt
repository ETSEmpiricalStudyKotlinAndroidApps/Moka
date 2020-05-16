package io.github.tonnyl.moka.ui.issue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.IssueComment
import io.github.tonnyl.moka.data.item.IssueTimelineItem
import io.github.tonnyl.moka.databinding.ItemIssueTimelineCommentBinding
import io.github.tonnyl.moka.databinding.ItemIssueTimelineEventBinding
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.reaction.ReactionChange
import io.github.tonnyl.moka.ui.reaction.ReactionGroupAdapter

class IssueTimelineAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val mainViewModel: MainViewModel,
    private val reactionViewPool: RecyclerView.RecycledViewPool
) : PagedListAdapter<IssueTimelineItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<IssueTimelineItem>() {

            override fun areItemsTheSame(
                oldItem: IssueTimelineItem,
                newItem: IssueTimelineItem
            ): Boolean = oldItem.areItemsTheSame(newItem)

            override fun areContentsTheSame(
                oldItem: IssueTimelineItem,
                newItem: IssueTimelineItem
            ): Boolean = oldItem.areContentsTheSame(newItem)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.item_issue_timeline_comment -> {
                CommentViewHolder(
                    lifecycleOwner,
                    mainViewModel,
                    ItemIssueTimelineCommentBinding.inflate(
                        inflater,
                        parent,
                        false
                    ).apply {
                        issueTimelineCommentReactions.setRecycledViewPool(reactionViewPool)
                    }
                )
            }
            R.layout.item_issue_timeline_event -> {
                EventViewHolder(
                    lifecycleOwner,
                    ItemIssueTimelineEventBinding.inflate(inflater, parent, false)
                )
            }
            else -> {
                throw IllegalArgumentException("unsupported view type: $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        val item = getItem(position) ?: return

        when (viewType) {
            R.layout.item_issue_timeline_comment -> {
                (holder as CommentViewHolder).bindTo(item as IssueComment)
            }
            R.layout.item_issue_timeline_event -> {
                (holder as EventViewHolder).bindTo(item)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val payload = payloads.firstOrNull()
        if (payload == null) {
            onBindViewHolder(holder, position)
        } else if (payload is ReactionChange
            && holder is CommentViewHolder
        ) {
            val recyclerView = holder.binding.issueTimelineCommentReactions
            val adapter = recyclerView.adapter as? ReactionGroupAdapter
            adapter?.updateUiByReactionChange(payload)
            recyclerView.smoothScrollToPosition(payload.position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is IssueComment -> {
                R.layout.item_issue_timeline_comment
            }
            else -> {
                R.layout.item_issue_timeline_event
            }
        }
    }

    class EventViewHolder(
        private val owner: LifecycleOwner,
        private val binding: ItemIssueTimelineEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: IssueTimelineItem) {
            with(binding) {
                lifecycleOwner = owner
                issueTimelineEvent = data

                executePendingBindings()
            }
        }

    }

    class CommentViewHolder(
        private val owner: LifecycleOwner,
        private val model: MainViewModel,
        val binding: ItemIssueTimelineCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: IssueComment) {
            with(binding) {
                lifecycleOwner = owner
                comment = data
                mainViewModel = model

                executePendingBindings()
            }
        }

    }

}