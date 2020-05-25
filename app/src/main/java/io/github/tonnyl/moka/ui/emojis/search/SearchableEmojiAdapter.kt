package io.github.tonnyl.moka.ui.emojis.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.SearchableEmoji
import io.github.tonnyl.moka.databinding.ItemSearchableEmojiBinding
import io.github.tonnyl.moka.ui.emojis.search.SearchableEmojiAdapter.SearchedEmojiViewHolder

class SearchableEmojiAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val searchEmojiViewModel: SearchEmojiViewModel
) : ListAdapter<SearchableEmoji, SearchedEmojiViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchedEmojiViewHolder {
        return SearchedEmojiViewHolder(
            lifecycleOwner,
            searchEmojiViewModel,
            ItemSearchableEmojiBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchedEmojiViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    class SearchedEmojiViewHolder(
        private val owner: LifecycleOwner,
        private val model: SearchEmojiViewModel,
        private val binding: ItemSearchableEmojiBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchableEmoji) {
            with(binding) {
                lifecycleOwner = owner
                searchEmojiViewModel = model
                emoji = item

                executePendingBindings()
            }
        }

    }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchableEmoji>() {

            override fun areItemsTheSame(
                oldItem: SearchableEmoji,
                newItem: SearchableEmoji
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: SearchableEmoji,
                newItem: SearchableEmoji
            ): Boolean {
                return oldItem.emoji == newItem.emoji
            }

        }

    }

}