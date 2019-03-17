package io.github.tonnyl.moka.ui.notifications

import android.text.style.ForegroundColorSpan
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.databinding.ItemNotificationBinding
import io.github.tonnyl.moka.net.NetworkState
import io.github.tonnyl.moka.net.Status
import kotlinx.android.synthetic.main.item_network_state.view.*

class NotificationAdapter(
        private val retryCallback: () -> Unit
) : PagedListAdapter<Notification, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private var networkState: NetworkState? = null

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Notification>() {

            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_network_state -> NetworkStateViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_network_state, parent, false), retryCallback)
        R.layout.item_notification -> NotificationViewHolder(ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else -> throw IllegalArgumentException("unknown view type $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_network_state -> {
                (holder as NetworkStateViewHolder).bind(networkState)
            }
            R.layout.item_notification -> {
                val item = getItem(position) ?: return
                with(holder as NotificationViewHolder) {
                    bindTo(item, position)
                    this.itemView.tag = item
                }
            }
        }
    }

    override fun getItemCount(): Int = super.getItemCount() + if (hasExtraRow()) 1 else 0

    override fun getItemViewType(position: Int): Int = if (hasExtraRow() && position == itemCount - 1) R.layout.item_network_state else R.layout.item_notification

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    class NotificationViewHolder(
            private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        private val reasonSpan = ForegroundColorSpan(ResourcesCompat.getColor(binding.root.resources, R.color.colorTextPrimary, null))

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            if (itemView.tag != null && itemView.tag is Notification) {
                val notification = itemView.tag as Notification
                menu?.setHeaderTitle(notification.subject.title)
                // trick: use layout position as order
                if (notification.unread) {
                    menu?.add(Menu.FIRST, R.id.notification_menu_mark_as_read, layoutPosition, R.string.notification_mark_as_read)
                }
                menu?.add(Menu.FIRST, R.id.notification_menu_unsubscribe, layoutPosition, R.string.notification_unsubscribe)
            }
        }

        fun bindTo(data: Notification, position: Int) {
            binding.apply {
                this.position = position
                span = reasonSpan
                timeInMillis = data.updatedAt.time
                title = data.subject.title
                reason = data.reason
                repositoryName = data.repository.fullName
                avatar = data.repository.owner.avatarUrl
            }

            binding.executePendingBindings()

            itemView.setOnCreateContextMenuListener(this)
        }

    }

    class NetworkStateViewHolder(
            view: View,
            private val retryCallback: () -> Unit
    ) : RecyclerView.ViewHolder(view) {

        companion object {

            fun toVisibility(constraint: Boolean): Int = if (constraint) View.VISIBLE else View.GONE

        }

        init {
            itemView.item_network_state_retry_button.setOnClickListener {
                retryCallback.invoke()
            }
        }

        fun bind(networkState: NetworkState?) {
            with(itemView) {
                item_network_state_progress_bar.visibility = toVisibility(networkState?.status == Status.LOADING)
                item_network_state_retry_button.visibility = toVisibility(networkState?.status == Status.ERROR)
                item_network_state_error_message.visibility = toVisibility(networkState?.msg.isNullOrEmpty().not())
                item_network_state_error_message.text = networkState?.msg
            }
        }

    }

}