package io.github.tonnyl.moka.ui.repository

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.RepositoryTopic
import io.github.tonnyl.moka.databinding.ItemRepositoryTopicBinding

class RepositoryTopicAdapter :
    ListAdapter<RepositoryTopic, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RepositoryTopic>() {

            override fun areItemsTheSame(
                oldItem: RepositoryTopic,
                newItem: RepositoryTopic
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: RepositoryTopic,
                newItem: RepositoryTopic
            ): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = RepositoryTopicViewHolder(ItemRepositoryTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        if (holder is RepositoryTopicViewHolder) {
            holder.bindTo(data)
        }
    }

    class RepositoryTopicViewHolder(
            private val binding: ItemRepositoryTopicBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: RepositoryTopic) {
            binding.apply {
                topic = data
            }

            binding.executePendingBindings()
        }

    }

}