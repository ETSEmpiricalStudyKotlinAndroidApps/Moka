package io.github.tonnyl.moka.ui.issues

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.databinding.ItemIssueBinding

class IssueAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: IssuesViewModel
) : PagedListAdapter<IssueItem, IssueAdapter.IssueViewHolder>(DIFF_CALLBACK) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueViewHolder {
        return IssueViewHolder(
            ItemIssueBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {
        val item = getItem(position) ?: return

        holder.bindTo(item, lifecycleOwner, viewModel)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_pull_request
    }

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