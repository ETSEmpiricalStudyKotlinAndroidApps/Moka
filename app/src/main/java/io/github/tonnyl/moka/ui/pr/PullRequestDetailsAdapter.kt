package io.github.tonnyl.moka.ui.pr

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.databinding.ItemPrTimelineHeadBinding
import io.github.tonnyl.moka.ui.MainViewModel

class PullRequestDetailsAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val mainViewModel: MainViewModel,
    private val pullRequestViewModel: PullRequestViewModel,
    private val reactionsViewPool: RecyclerView.RecycledViewPool
) : RecyclerView.Adapter<PullRequestDetailsAdapter.PullRequestDetailsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PullRequestDetailsViewHolder {
        return PullRequestDetailsViewHolder(
            lifecycleOwner,
            mainViewModel,
            pullRequestViewModel,
            ItemPrTimelineHeadBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).apply {
                prCommentReactions.setRecycledViewPool(reactionsViewPool)
            }
        )
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: PullRequestDetailsViewHolder, position: Int) {
        holder.bind()
    }

    class PullRequestDetailsViewHolder(
        private val owner: LifecycleOwner,
        private val mainModel: MainViewModel,
        private val prModel: PullRequestViewModel,
        private val binding: ItemPrTimelineHeadBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            with(binding) {
                lifecycleOwner = owner
                pullRequestViewModel = prModel
                mainViewModel = mainModel

                executePendingBindings()
            }
        }

    }

}