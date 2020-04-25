package io.github.tonnyl.moka.ui.issue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.databinding.ItemIssueTimelineHeadBinding

class IssueDetailsAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val issueViewModel: IssueViewModel,
    private val reactionsViewPool: RecyclerView.RecycledViewPool
) : RecyclerView.Adapter<IssueDetailsAdapter.IssueDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueDetailsViewHolder {
        return IssueDetailsViewHolder(
            lifecycleOwner,
            issueViewModel,
            ItemIssueTimelineHeadBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).apply {
                issueCommentReactions.setRecycledViewPool(reactionsViewPool)
            }
        )
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: IssueDetailsViewHolder, position: Int) {
        holder.bind()
    }

    class IssueDetailsViewHolder(
        private val owner: LifecycleOwner,
        private val model: IssueViewModel,
        private val binding: ItemIssueTimelineHeadBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            with(binding) {
                lifecycleOwner = owner
                viewModel = model

                executePendingBindings()
            }
        }

    }

}