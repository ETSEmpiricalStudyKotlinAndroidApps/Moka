package io.github.tonnyl.moka.ui.issues

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.databinding.ItemIssueBinding
import io.github.tonnyl.moka.ui.PagedResourceAdapter
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class IssueAdapter(
    private val actions: IssueItemActions,
    override val retryActions: PagingNetworkStateActions
) : PagedResourceAdapter<IssueItem>(DIFF_CALLBACK, retryActions) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<IssueItem>() {

            override fun areItemsTheSame(oldItem: IssueItem, newItem: IssueItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: IssueItem, newItem: IssueItem): Boolean {
                return oldItem == newItem
            }

        }

    }

    override fun initiateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return IssueViewHolder(
            ItemIssueBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), actions
        )
    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (holder is IssueViewHolder) {
            holder.bindTo(item)
        }
    }

    override fun getViewType(position: Int): Int = R.layout.item_pull_request

    class IssueViewHolder(
        private val binding: ItemIssueBinding,
        private val issueItemActions: IssueItemActions?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: IssueItem) {
            binding.run {
                issue = data
                actions = issueItemActions
                executePendingBindings()
            }
        }

    }

}