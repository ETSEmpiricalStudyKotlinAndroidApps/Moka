package io.github.tonnyl.moka.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.ItemLoadStateBinding
import io.github.tonnyl.moka.network.NetworkState

class LoadStateAdapter(
    val actions: PagingNetworkStateActions
) : RecyclerView.Adapter<LoadStateViewHolder>() {

    var loadState: NetworkState = NetworkState.LOADED
        set(value) {
            if (field != value) {
                val displayOldItem = displayLoadStateAsItem(field)
                val displayNewItem = displayLoadStateAsItem(value)

                if (displayOldItem && !displayNewItem) {
                    notifyItemRemoved(0)
                } else if (displayNewItem && !displayOldItem) {
                    notifyItemInserted(0)
                } else if (displayOldItem && displayNewItem) {
                    notifyItemChanged(0)
                }

                field = value
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadStateViewHolder {
        return LoadStateViewHolder(
            ItemLoadStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, position: Int) {
        holder.bindTo(loadState, actions)
    }

    override fun getItemCount(): Int {
        return if (displayLoadStateAsItem(loadState)) {
            1
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_load_state
    }

    private fun displayLoadStateAsItem(state: NetworkState): Boolean {
        return state != NetworkState.LOADED
    }

}