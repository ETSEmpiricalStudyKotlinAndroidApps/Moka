package io.github.tonnyl.moka.ui.reaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.ReactionGroup
import io.github.tonnyl.moka.databinding.ItemReactionGroupBinding
import io.github.tonnyl.moka.ui.reaction.ReactionChange.*

class ReactionGroupAdapter(
    private val viewerCanReact: Boolean
) : ListAdapter<ReactionGroup, ReactionGroupAdapter.ReactionGroupViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ReactionGroup>() {

            override fun areItemsTheSame(oldItem: ReactionGroup, newItem: ReactionGroup): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: ReactionGroup,
                newItem: ReactionGroup
            ): Boolean {
                return false
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionGroupViewHolder {
        return ReactionGroupViewHolder(
            viewerCanReact,
            ItemReactionGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ReactionGroupViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    fun updateUiByReactionChange(reactionChange: ReactionChange) {
        val position = reactionChange.position
        when (reactionChange) {
            is ReactionUpdate -> {
                notifyItemChanged(position)
            }
            is ReactionRemove -> {
                notifyItemRemoved(position)
            }
            is ReactionInsert -> {
                notifyItemInserted(position)
            }
        }

    }

    class ReactionGroupViewHolder(
        private val isViewerCanReact: Boolean,
        private val binding: ItemReactionGroupBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ReactionGroup) {
            with(binding) {
                reactionGroup = data
                viewerCanReact = isViewerCanReact

                executePendingBindings()
            }
        }

    }

}