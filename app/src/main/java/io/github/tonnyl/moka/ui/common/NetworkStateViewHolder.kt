package io.github.tonnyl.moka.ui.common

import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.databinding.ItemNetworkStateBinding
import io.github.tonnyl.moka.network.NetworkState
import kotlinx.android.synthetic.main.item_network_state.view.*

class NetworkStateViewHolder(
        private val binding: ItemNetworkStateBinding,
        private val retryCallback: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.item_network_state_retry_button.setOnClickListener {
            retryCallback.invoke()
        }
    }

    fun bindTo(networkState: NetworkState?) {
        binding.apply {
            state = networkState
        }

        binding.executePendingBindings()
    }

}