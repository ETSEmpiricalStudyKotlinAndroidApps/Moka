package io.github.tonnyl.moka.ui

import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.databinding.ItemNetworkStateBinding
import io.github.tonnyl.moka.network.NetworkState

class NetworkStateViewHolder(
    private val binding: ItemNetworkStateBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindTo(networkState: NetworkState?, actions: PagingNetworkStateActions) {
        binding.apply {
            state = networkState
            this.actions = actions
        }

        binding.executePendingBindings()
    }

}