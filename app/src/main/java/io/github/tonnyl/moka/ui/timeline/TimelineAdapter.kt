package io.github.tonnyl.moka.ui.timeline

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.databinding.ItemEventBinding
import io.github.tonnyl.moka.ui.PagedResourceAdapter
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class TimelineAdapter(
    override val retryActions: PagingNetworkStateActions
) : PagedResourceAdapter<Event>(DIFF_CALLBACK, retryActions) {

    var eventActions: EventActions? = null

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Event>() {

            override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem == newItem
            }

        }

        const val VIEW_TYPE_EVENT = 0x00

    }

    override fun initiateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EventViewHolder(
            ItemEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (holder is EventViewHolder) {
            holder.bind(item, eventActions)
        }
    }

    override fun getViewType(position: Int): Int = VIEW_TYPE_EVENT

    class EventViewHolder(
        private val binding: ItemEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Event, actions: EventActions?) {
            binding.apply {
                event = data
                eventActions = actions

                eventAction.movementMethod = LinkMovementMethod.getInstance()
            }

            binding.executePendingBindings()
        }

    }

}