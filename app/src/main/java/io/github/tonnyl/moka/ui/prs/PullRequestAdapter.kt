package io.github.tonnyl.moka.ui.prs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.databinding.ItemPullRequestBinding
import io.github.tonnyl.moka.ui.PagedResourceAdapter
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class PullRequestAdapter(
    private val actions: PullRequestItemActions,
    override val retryActions: PagingNetworkStateActions
) : PagedResourceAdapter<PullRequestItem>(DIFF_CALLBACK, retryActions) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PullRequestItem>() {

            override fun areItemsTheSame(
                oldItem: PullRequestItem,
                newItem: PullRequestItem
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: PullRequestItem,
                newItem: PullRequestItem
            ): Boolean = oldItem == newItem

        }

    }

    override fun initiateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PullRequestViewHolder(
            ItemPullRequestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), actions
        )
    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (holder is PullRequestViewHolder) {
            holder.bindTo(item)
        }
    }

    override fun getViewType(position: Int): Int = R.layout.item_pull_request

    class PullRequestViewHolder(
        private val binding: ItemPullRequestBinding,
        private val pullRequestItemActions: PullRequestItemActions?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: PullRequestItem) {
            binding.apply {
                pullRequest = data
                actions = pullRequestItemActions
            }

            binding.executePendingBindings()
        }

    }

}