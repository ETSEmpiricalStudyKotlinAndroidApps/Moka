package io.github.tonnyl.moka.ui.emojis

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Emoji
import io.github.tonnyl.moka.data.EmojiCategory
import io.github.tonnyl.moka.data.EmojiType
import io.github.tonnyl.moka.databinding.ItemEmojiBinding
import io.github.tonnyl.moka.databinding.ItemEmojiCategoryBinding

class EmojiAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val emojisViewModel: EmojisViewModel
) : ListAdapter<EmojiType, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_EMOJI -> {
                EmojiViewHolder(
                    lifecycleOwner,
                    emojisViewModel,
                    ItemEmojiBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_EMOJI_CATEGORY -> {
                EmojiCategoryViewHolder(
                    lifecycleOwner,
                    ItemEmojiCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                throw IllegalArgumentException("Unknown item type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EmojiViewHolder -> {
                holder.bind(getItem(position) as Emoji)
            }
            is EmojiCategoryViewHolder -> {
                holder.bind(getItem(position) as EmojiCategory)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Emoji -> {
                VIEW_TYPE_EMOJI
            }
            is EmojiCategory -> {
                VIEW_TYPE_EMOJI_CATEGORY
            }
            else -> {
                throw IllegalArgumentException("Unknown item type")
            }
        }
    }

    class EmojiViewHolder(
        private val owner: LifecycleOwner,
        private val model: EmojisViewModel,
        private val binding: ItemEmojiBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Emoji) {
            with(binding) {
                emoji = item
                lifecycleOwner = owner
                emojisViewModel = model

                executePendingBindings()
            }
        }

    }

    class EmojiCategoryViewHolder(
        private val owner: LifecycleOwner,
        private val binding: ItemEmojiCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EmojiCategory) {
            with(binding) {
                emojiCategory = item
                lifecycleOwner = owner

                executePendingBindings()
            }
        }

    }

    companion object {

        const val VIEW_TYPE_EMOJI = R.layout.item_emoji
        const val VIEW_TYPE_EMOJI_CATEGORY = R.layout.item_emoji_category

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EmojiType>() {

            override fun areItemsTheSame(oldItem: EmojiType, newItem: EmojiType): Boolean {
                return when {
                    oldItem is Emoji && newItem is Emoji -> {
                        oldItem.emoji == newItem.emoji
                    }
                    oldItem is EmojiCategory && newItem is EmojiCategory -> {
                        oldItem.name == newItem.name
                    }
                    else -> {
                        false
                    }
                }
            }

            override fun areContentsTheSame(oldItem: EmojiType, newItem: EmojiType): Boolean {
                return when {
                    oldItem is Emoji && newItem is Emoji -> {
                        (oldItem as Emoji) == (newItem as Emoji)
                    }
                    oldItem is EmojiCategory && newItem is EmojiCategory -> {
                        (oldItem as EmojiCategory) == (newItem as EmojiCategory)
                    }
                    else -> {
                        false
                    }
                }
            }

        }

    }

}