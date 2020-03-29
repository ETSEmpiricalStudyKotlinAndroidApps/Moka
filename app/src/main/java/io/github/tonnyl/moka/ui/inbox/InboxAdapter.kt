package io.github.tonnyl.moka.ui.inbox

import android.view.*
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.databinding.ItemInboxNotificationBinding
import io.github.tonnyl.moka.ui.PagedResourceAdapter
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class InboxAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: InboxViewModel,
    override val retryActions: PagingNetworkStateActions
) : PagedResourceAdapter<Notification>(DIFF_CALLBACK, retryActions) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Notification>() {

            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return oldItem == newItem
            }

        }

    }

    override fun initiateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationViewHolder(
            lifecycleOwner,
            viewModel,
            ItemInboxNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        with(holder as NotificationViewHolder) {
            bindTo(item)
            this.itemView.tag = item
        }
    }

    override fun getViewType(position: Int): Int = R.layout.item_inbox_notification

    class NotificationViewHolder(
        private val owner: LifecycleOwner,
        private val model: InboxViewModel,
        private val binding: ItemInboxNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            if (itemView.tag != null && itemView.tag is Notification) {
                val notification = itemView.tag as Notification
                menu?.setHeaderTitle(notification.subject.title)
                // trick: use layout position as order
                if (notification.unread) {
                    menu?.add(
                        Menu.FIRST,
                        R.id.notification_menu_mark_as_read,
                        layoutPosition,
                        R.string.notification_mark_as_read
                    )
                }
                menu?.add(
                    Menu.FIRST,
                    R.id.notification_menu_unsubscribe,
                    layoutPosition,
                    R.string.notification_unsubscribe
                )
            }
        }

        fun bindTo(data: Notification) {
            with(binding) {
                lifecycleOwner = owner
                viewModel = model
                notification = data

                executePendingBindings()
            }

            itemView.setOnCreateContextMenuListener(this)
        }

    }

}