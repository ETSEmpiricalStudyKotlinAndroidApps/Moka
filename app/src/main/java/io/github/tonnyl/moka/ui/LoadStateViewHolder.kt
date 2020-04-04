package io.github.tonnyl.moka.ui

import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.databinding.ItemLoadStateBinding
import io.github.tonnyl.moka.network.NetworkState

class LoadStateViewHolder(
    private val binding: ItemLoadStateBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindTo(loadState: NetworkState?, actions: PagingNetworkStateActions) {
        binding.apply {
            state = loadState
            this.actions = actions
        }

        binding.executePendingBindings()
    }

}