package io.github.tonnyl.moka.ui.issue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.IssueCommentEvent
import io.github.tonnyl.moka.data.item.IssueTimelineItem
import io.github.tonnyl.moka.databinding.ItemIssueTimelineCommentBinding
import io.github.tonnyl.moka.databinding.ItemIssueTimelineEventBinding
import io.github.tonnyl.moka.databinding.ItemIssueTimelineHeadBinding
import io.github.tonnyl.moka.ui.PagedResourceAdapter
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class IssueTimelineAdapter(
    private val title: String,
    private val info: String,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: IssueViewModel,
    override val retryActions: PagingNetworkStateActions
) : PagedResourceAdapter<IssueTimelineItem>(DIFF_CALLBACK, retryActions) {

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

    override fun initiateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.item_issue_timeline_head -> {
                HeadViewHolder(
                    ItemIssueTimelineHeadBinding.inflate(inflater, parent, false)
                )
            }
            R.layout.item_issue_timeline_comment -> {
                CommentViewHolder(
                    ItemIssueTimelineCommentBinding.inflate(inflater, parent, false)
                )
            }
            R.layout.item_issue_timeline_event -> {
                EventViewHolder(
                    ItemIssueTimelineEventBinding.inflate(inflater, parent, false)
                )
            }
            else -> {
                throw IllegalArgumentException("unsupported view type: $viewType")
            }
        }
    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getViewType(position)
        if (viewType == R.layout.item_issue_timeline_head) {
            (holder as HeadViewHolder).bindTo(title, info, lifecycleOwner, viewModel)

            return
        }

        val item = getItem(position - 1) ?: return
        when (viewType) {
            R.layout.item_issue_timeline_comment -> {
                (holder as CommentViewHolder).bindTo(item as IssueCommentEvent)
            }
            R.layout.item_issue_timeline_event -> {
                (holder as EventViewHolder).bindTo(item)
            }
        }
    }

    override fun getViewType(position: Int): Int {
        return if (position == 0) {
            R.layout.item_issue_timeline_head
        } else {
            when (getItem(position - 1)) {
                is IssueCommentEvent -> {
                    R.layout.item_issue_timeline_comment
                }
                else -> {
                    R.layout.item_issue_timeline_event
                }
            }
        }
    }

    override fun getItemCount(): Int = super.getItemCount() + 1

    class EventViewHolder(
        private val binding: ItemIssueTimelineEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: IssueTimelineItem) {
            binding.run {
                issueTimelineEvent = data
                executePendingBindings()
            }
        }

    }

    class CommentViewHolder(
        private val binding: ItemIssueTimelineCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: IssueCommentEvent) {
            binding.run {
                comment = data
                executePendingBindings()
            }
        }

    }

    class HeadViewHolder(
        private val binding: ItemIssueTimelineHeadBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(
            title: String,
            info: String,
            lifecycleOwner: LifecycleOwner,
            viewModel: IssueViewModel
        ) {
            binding.run {
                this.title = title
                this.info = info
                this.lifecycleOwner = lifecycleOwner
                this.viewModel = viewModel
                executePendingBindings()
            }
        }

    }

}