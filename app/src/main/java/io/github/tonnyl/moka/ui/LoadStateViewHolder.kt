package io.github.tonnyl.moka.ui

import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.databinding.ItemLoadStateBinding

class LoadStateViewHolder(
    private val binding: ItemLoadStateBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.itemNetworkStateRetryButton.setOnClickListener {
            retry.invoke()
        }
    }

    fun bind(state: LoadState) {
        with(binding) {
            loadState = state

            executePendingBindings()
        }
    }

}