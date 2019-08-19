package io.github.tonnyl.moka.ui.pr

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.databinding.ItemPrTimelineCommentBinding
import io.github.tonnyl.moka.databinding.ItemPrTimelineEventBinding
import io.github.tonnyl.moka.databinding.ItemPrTimelineHeadBinding
import io.github.tonnyl.moka.databinding.ItemPrTimelineThreadBinding
import io.github.tonnyl.moka.ui.PagedResourceAdapter
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class PullRequestTimelineAdapter(
    private val title: String,
    private val info: String,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: PullRequestViewModel,
    override val retryActions: PagingNetworkStateActions
) : PagedResourceAdapter<PullRequestTimelineItem>(DIFF_CALLBACK, retryActions) {

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

    override fun initiateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.item_pr_timeline_head -> {
                HeadViewHolder(ItemPrTimelineHeadBinding.inflate(inflater, parent, false))
            }
            R.layout.item_pr_timeline_comment -> {
                CommentViewHolder(ItemPrTimelineCommentBinding.inflate(inflater, parent, false))
            }
            R.layout.item_pr_timeline_thread -> {
                ThreadViewHolder(ItemPrTimelineThreadBinding.inflate(inflater, parent, false))
            }
            R.layout.item_pr_timeline_event -> {
                EventViewHolder(ItemPrTimelineEventBinding.inflate(inflater, parent, false))
            }
            else -> {
                throw IllegalArgumentException("unsupported view type: $viewType")
            }
        }
    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getViewType(position)
        if (viewType == R.layout.item_pr_timeline_head) {
            (holder as HeadViewHolder).bindTo(title, info, lifecycleOwner, viewModel)

            return
        }

        val item = getItem(position - 1) ?: return
        when (viewType) {
            R.layout.item_pr_timeline_comment -> {
                (holder as CommentViewHolder).bindTo(item)
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

    override fun getViewType(position: Int): Int {
        return if (position == 0) {
            R.layout.item_pr_timeline_head
        } else {
            when (getItem(position - 1)) {
                is PullRequestIssueComment,
                is PullRequestReviewComment -> {
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
    }

    override fun getItemCount(): Int = super.getItemCount() + 1

    class CommentViewHolder(
        private val binding: ItemPrTimelineCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: PullRequestTimelineItem) {
            binding.run {
                comment = data
                executePendingBindings()
            }
        }

    }

    class EventViewHolder(
        private val binding: ItemPrTimelineEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: PullRequestTimelineItem) {
            binding.run {
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

    class HeadViewHolder(
        private val binding: ItemPrTimelineHeadBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(
            title: String,
            info: String,
            lifecycleOwner: LifecycleOwner,
            viewModel: PullRequestViewModel
        ) {
            binding.run {
                this.title = title
                this.info = info
                this.viewModel = viewModel
                this.lifecycleOwner = lifecycleOwner
                executePendingBindings()
            }
        }

    }

}