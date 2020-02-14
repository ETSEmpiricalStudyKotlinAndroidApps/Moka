package io.github.tonnyl.moka.ui.inbox

import android.text.style.ForegroundColorSpan
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.databinding.ItemInboxNotificationBinding
import io.github.tonnyl.moka.ui.PagedResourceAdapter
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class InboxAdapter(
    override val retryActions: PagingNetworkStateActions
) : PagedResourceAdapter<Notification>(DIFF_CALLBACK, retryActions) {

    var inboxActions: InboxActions? = null

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Notification>() {

            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return oldItem == newItem
            }

        }

        const val VIEW_TYPE_NOTIFICATION = R.layout.item_inbox_notification

    }

    override fun initiateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationViewHolder(
            ItemInboxNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        with(holder as NotificationViewHolder) {
            bindTo(item, inboxActions)
            this.itemView.tag = item
        }
    }

    override fun getViewType(position: Int): Int = VIEW_TYPE_NOTIFICATION

    class NotificationViewHolder(
        private val binding: ItemInboxNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        private val reasonSpan = ForegroundColorSpan(
            ResourcesCompat.getColor(
                binding.root.resources,
                R.color.colorTextPrimary,
                null
            )
        )

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

        fun bindTo(data: Notification, actions: InboxActions?) {
            binding.apply {
                this.notification = data
                notificationActions = actions
                span = reasonSpan
            }

            binding.executePendingBindings()

            itemView.setOnCreateContextMenuListener(this)
        }

    }

}