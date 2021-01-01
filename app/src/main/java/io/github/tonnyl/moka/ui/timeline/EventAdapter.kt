package io.github.tonnyl.moka.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.databinding.ItemEventBinding

@ExperimentalPagingApi
class EventAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: TimelineViewModel
) : PagingDataAdapter<Event, EventAdapter.EventViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(
            lifecycleOwner,
            viewModel,
            ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
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

}