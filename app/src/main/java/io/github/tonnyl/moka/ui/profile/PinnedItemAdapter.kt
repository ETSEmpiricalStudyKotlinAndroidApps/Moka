package io.github.tonnyl.moka.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Gist2
import io.github.tonnyl.moka.data.PinnableItem
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.databinding.ItemPinnedGistBinding
import io.github.tonnyl.moka.databinding.ItemPinnedRepositoryBinding

class PinnedItemAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val profileViewModel: ProfileViewModel
) : ListAdapter<PinnableItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_pinned_repository -> {
                RepositoryViewHolder(
                    ItemPinnedRepositoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    lifecycleOwner,
                    profileViewModel
                )
            }
            R.layout.item_pinned_gist -> {
                GistViewHolder(
                    ItemPinnedGistBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    lifecycleOwner,
                    profileViewModel
                )
            }
            else -> {
                throw IllegalStateException("invalid view type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        if (holder is RepositoryViewHolder) {
            holder.bind(item as RepositoryItem)
        } else if (holder is GistViewHolder) {
            holder.bind(item as Gist2)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RepositoryItem -> {
                R.layout.item_pinned_repository
            }
            is Gist2 -> {
                R.layout.item_pinned_gist
            }
            else -> {
                throw IllegalStateException("invalid view type")
            }
        }
    }

    class RepositoryViewHolder(
        private val binding: ItemPinnedRepositoryBinding,
        private val owner: LifecycleOwner,
        private val model: ProfileViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: RepositoryItem) {
            with(binding) {
                lifecycleOwner = owner
                profileViewModel = model
                repository = data

                executePendingBindings()
            }
        }

    }

    class GistViewHolder(
        private val binding: ItemPinnedGistBinding,
        private val owner: LifecycleOwner,
        private val model: ProfileViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Gist2) {
            with(binding) {
                gist = data
                lifecycleOwner = owner
                profileViewModel = model

                executePendingBindings()
            }
        }

    }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PinnableItem>() {

            override fun areItemsTheSame(oldItem: PinnableItem, newItem: PinnableItem): Boolean {
                return when {
                    oldItem is RepositoryItem && newItem is RepositoryItem -> {
                        oldItem.id == newItem.id
                    }
                    oldItem is Gist2 && newItem is Gist2 -> {
                        oldItem.id == newItem.id
                    }
                    else -> {
                        false
                    }
                }
            }

            override fun areContentsTheSame(oldItem: PinnableItem, newItem: PinnableItem): Boolean {
                return when {
                    oldItem is RepositoryItem && newItem is RepositoryItem -> {
                        (oldItem as RepositoryItem) == (newItem as RepositoryItem)
                    }
                    oldItem is Gist2 && newItem is Gist2 -> {
                        (oldItem as Gist2) == (newItem as Gist2)
                    }
                    else -> {
                        false
                    }
                }
            }

        }

    }

}