package io.github.tonnyl.moka.ui.timeline

import androidx.recyclerview.widget.DiffUtil
import io.github.tonnyl.moka.data.Event

class EventDiffCallback : DiffUtil.ItemCallback<Event>() {

    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem == newItem

}