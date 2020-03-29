package io.github.tonnyl.moka.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.databinding.ItemEventBinding
import io.github.tonnyl.moka.ui.PagedResourceAdapter
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class EventAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: TimelineViewModel,
    override val retryActions: PagingNetworkStateActions
) : PagedResourceAdapter<Event>(DIFF_CALLBACK, retryActions) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Event>() {

            override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem == newItem
            }

        }

    }

    override fun initiateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EventViewHolder(
            lifecycleOwner,
            viewModel,
            ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (holder is EventViewHolder) {
            holder.bind(item)
        }
    }

    override fun getViewType(position: Int): Int {
        return R.layout.item_event
    }

    class EventViewHolder(
        private val owner: LifecycleOwner,
        private val model: TimelineViewModel,
        private val binding: ItemEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Event) {
            with(binding) {
                lifecycleOwner = owner
                viewModel = model
                event = data

                executePendingBindings()
            }
        }

    }

}