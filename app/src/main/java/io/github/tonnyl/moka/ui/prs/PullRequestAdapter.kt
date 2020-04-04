package io.github.tonnyl.moka.ui.prs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.databinding.ItemPullRequestBinding

class PullRequestAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: PullRequestsViewModel
) : PagedListAdapter<PullRequestItem, PullRequestAdapter.PullRequestViewHolder>(DIFF_CALLBACK) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PullRequestViewHolder {
        return PullRequestViewHolder(
            lifecycleOwner,
            viewModel,
            ItemPullRequestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PullRequestViewHolder, position: Int) {
        val item = getItem(position) ?: return

        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_pull_request

    class PullRequestViewHolder(
        private val owner: LifecycleOwner,
        private val model: PullRequestsViewModel,
        private val binding: ItemPullRequestBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pr: PullRequestItem) {
            with(binding) {
                lifecycleOwner = owner
                viewModel = model
                pullRequest = pr

                executePendingBindings()
            }
        }

    }

}