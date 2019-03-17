package io.github.tonnyl.moka.ui.issues

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.databinding.ItemIssuePrBinding
import io.github.tonnyl.moka.ui.common.IssuePRActions

class IssueAdapter : PagedListAdapter<IssueItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    var actions: IssuePRActions? = null

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<IssueItem>() {

            override fun areItemsTheSame(oldItem: IssueItem, newItem: IssueItem): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: IssueItem, newItem: IssueItem): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = IssueViewHolder(ItemIssuePrBinding.inflate(LayoutInflater.from(parent.context), parent, false), actions)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (holder is IssueViewHolder) {
            holder.bindTo(item)
        }
    }

    class IssueViewHolder(
            private val binding: ItemIssuePrBinding,
            private val issuePRActions: IssuePRActions?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: IssueItem) {
            binding.apply {
                avatar = data.avatarUrl?.toString()
                number = data.number
                login = data.login
                title = data.title
                updateTimeInMillis = data.createdAt.time
                statusDrawableResId = if (data.closed) R.drawable.ic_issue_closed_24 else R.drawable.ic_issue_open_24
                actions = issuePRActions
            }

            binding.executePendingBindings()
        }

    }

}