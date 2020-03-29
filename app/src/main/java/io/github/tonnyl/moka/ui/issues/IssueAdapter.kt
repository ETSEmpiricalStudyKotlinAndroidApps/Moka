package io.github.tonnyl.moka.ui.issues

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.databinding.ItemIssueBinding
import io.github.tonnyl.moka.ui.PagedResourceAdapter
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class IssueAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: IssuesViewModel,
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
            )
        )
    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (holder is IssueViewHolder) {
            holder.bindTo(item, lifecycleOwner, viewModel)
        }
    }

    override fun getViewType(position: Int): Int = R.layout.item_pull_request

    class IssueViewHolder(
        private val binding: ItemIssueBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(
            data: IssueItem,
            owner: LifecycleOwner,
            model: IssuesViewModel
        ) {
            binding.run {
                issue = data
                lifecycleOwner = owner
                viewModel = model
                executePendingBindings()
            }
        }

    }

}