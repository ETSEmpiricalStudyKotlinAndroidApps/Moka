package io.github.tonnyl.moka.ui.prs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.databinding.ItemIssuePrBinding
import io.github.tonnyl.moka.ui.common.IssuePRActions

class PullRequestAdapter : PagedListAdapter<PullRequestItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    var actions: IssuePRActions? = null

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PullRequestItem>() {

            override fun areItemsTheSame(oldItem: PullRequestItem, newItem: PullRequestItem): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PullRequestItem, newItem: PullRequestItem): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = PullRequestViewHolder(ItemIssuePrBinding.inflate(LayoutInflater.from(parent.context), parent, false), actions)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (holder is PullRequestViewHolder) {
            holder.bindTo(item)
        }
    }

    class PullRequestViewHolder(
            private val binding: ItemIssuePrBinding,
            private val issuePRActions: IssuePRActions?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: PullRequestItem) {
            binding.apply {
                avatar = data.avatarUrl?.toString()
                number = data.number
                login = data.login
                title = data.title
                updateTimeInMillis = data.createdAt.time
                statusDrawableResId = if (data.merged) R.drawable.ic_pr_merged else if (data.closed) R.drawable.ic_pr_closed else R.drawable.ic_pr_open
                actions = issuePRActions
            }

            binding.executePendingBindings()
        }

    }

}