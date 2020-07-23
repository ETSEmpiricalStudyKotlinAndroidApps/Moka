package io.github.tonnyl.moka.ui.pr

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.IssueComment
import io.github.tonnyl.moka.data.item.PullRequestCommitCommentThread
import io.github.tonnyl.moka.data.item.PullRequestReviewThread
import io.github.tonnyl.moka.data.item.PullRequestTimelineItem
import io.github.tonnyl.moka.databinding.ItemPrTimelineCommentBinding
import io.github.tonnyl.moka.databinding.ItemPrTimelineEventBinding
import io.github.tonnyl.moka.databinding.ItemPrTimelineThreadBinding
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.reaction.ReactionChange
import io.github.tonnyl.moka.ui.reaction.ReactionGroupAdapter

class PullRequestTimelineAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val mainViewModel: MainViewModel,
    private val reactionsViewPool: RecyclerView.RecycledViewPool
) : PagingDataAdapter<PullRequestTimelineItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.item_pr_timeline_comment -> {
                CommentViewHolder(
                    lifecycleOwner,
                    mainViewModel,
                    ItemPrTimelineCommentBinding.inflate(inflater, parent, false).apply {
                        issueTimelineCommentReactions.apply {
                            setRecycledViewPool(reactionsViewPool)
                        }
                    }
                )
            }
            R.layout.item_pr_timeline_thread -> {
                ThreadViewHolder(ItemPrTimelineThreadBinding.inflate(inflater, parent, false))
            }
            R.layout.item_pr_timeline_event -> {
                EventViewHolder(
                    lifecycleOwner,
                    ItemPrTimelineEventBinding.inflate(inflater, parent, false)
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
            R.layout.item_pr_timeline_comment -> {
                (holder as CommentViewHolder).bindTo(item as IssueComment)
            }
            R.layout.item_pr_timeline_thread -> {
                (holder as ThreadViewHolder).bindTo(item)
            }
            R.layout.item_pr_timeline_event -> {
                (holder as EventViewHolder).bindTo(item)
            }
            else -> {
                throw IllegalArgumentException("unsupported view type: $viewType")
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
                R.layout.item_pr_timeline_comment
            }
            is PullRequestReviewThread,
            is PullRequestCommitCommentThread -> {
                R.layout.item_pr_timeline_thread
            }
            else -> {
                R.layout.item_pr_timeline_event
            }
        }
    }

    class CommentViewHolder(
        private val owner: LifecycleOwner,
        private val mainModel: MainViewModel,
        val binding: ItemPrTimelineCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: IssueComment) {
            with(binding) {
                lifecycleOwner = owner
                comment = data
                mainViewModel = mainModel

                executePendingBindings()
            }
        }

    }

    class EventViewHolder(
        private val owner: LifecycleOwner,
        private val binding: ItemPrTimelineEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: PullRequestTimelineItem) {
            with(binding) {
                lifecycleOwner = owner
                prTimelineEvent = data

                executePendingBindings()
            }
        }

    }

    class ThreadViewHolder(
        private val binding: ItemPrTimelineThreadBinding
    ) : RecyclerView.ViewHolder(binding.root) {

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

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PullRequestTimelineItem>() {

            override fun areItemsTheSame(
                oldItem: PullRequestTimelineItem,
                newItem: PullRequestTimelineItem
            ): Boolean = oldItem.areItemsTheSame(newItem)

            override fun areContentsTheSame(
                oldItem: PullRequestTimelineItem,
                newItem: PullRequestTimelineItem
            ): Boolean = oldItem.areContentsTheSame(newItem)

        }

    }

}