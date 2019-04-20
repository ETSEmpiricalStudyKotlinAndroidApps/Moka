package io.github.tonnyl.moka.ui.notifications

import android.text.style.ForegroundColorSpan
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.databinding.ItemNetworkStateBinding
import io.github.tonnyl.moka.databinding.ItemNotificationBinding
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.ui.common.NetworkStateViewHolder

class NotificationAdapter(
        private val beforeRetryCallback: () -> Unit,
        private val afterRetryCallback: () -> Unit
) : PagedListAdapter<Notification, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private var beforeNetworkState: NetworkState? = null
    private var afterNetworkState: NetworkState? = null

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Notification>() {

            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean = oldItem == newItem

        }

        const val VIEW_TYPE_BEFORE_NETWORK_STATE = 0x00
        const val VIEW_TYPE_AFTER_NETWORK_STATE = 0x01
        const val VIEW_TYPE_NOTIFICATION = 0x02

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_BEFORE_NETWORK_STATE -> {
                NetworkStateViewHolder(ItemNetworkStateBinding.inflate(inflater, parent, false), beforeRetryCallback)
            }
            VIEW_TYPE_AFTER_NETWORK_STATE -> {
                NetworkStateViewHolder(ItemNetworkStateBinding.inflate(inflater, parent, false), afterRetryCallback)
            }
            VIEW_TYPE_NOTIFICATION -> {
                NotificationViewHolder(ItemNotificationBinding.inflate(inflater, parent, false))
            }
            else -> {
                throw IllegalArgumentException("Invalid view type: $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_AFTER_NETWORK_STATE -> {
                (holder as NetworkStateViewHolder).bindTo(afterNetworkState)
            }
            VIEW_TYPE_BEFORE_NETWORK_STATE -> {
                (holder as NetworkStateViewHolder).bindTo(beforeNetworkState)
            }
            VIEW_TYPE_NOTIFICATION -> {
                val item = getItem(position) ?: return
                with(holder as NotificationViewHolder) {
                    bindTo(item, position)
                    this.itemView.tag = item
                }
            }
        }
    }

    override fun getItemCount(): Int = super.getItemCount() + if (hasBeforeExtraRow()) 1 else 0 + if (hasAfterExtraRow()) 1 else 0

    override fun getItemViewType(position: Int): Int = when {
        hasBeforeExtraRow() && position == 0 -> VIEW_TYPE_BEFORE_NETWORK_STATE
        hasAfterExtraRow() && position == itemCount - 1 -> VIEW_TYPE_AFTER_NETWORK_STATE
        else -> VIEW_TYPE_NOTIFICATION
    }

    private fun hasBeforeExtraRow() = beforeNetworkState != null && beforeNetworkState != NetworkState.LOADED

    private fun hasAfterExtraRow() = afterNetworkState != null && afterNetworkState != NetworkState.LOADED

    fun setBeforeNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.beforeNetworkState
        val hadExtraRow = hasBeforeExtraRow()
        this.beforeNetworkState = newNetworkState
        val hasExtraRow = hasBeforeExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(0)
        }
    }

    fun setAfterNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.afterNetworkState
        val hadExtraRow = hasAfterExtraRow()
        this.afterNetworkState = newNetworkState
        val hasExtraRow = hasAfterExtraRow()
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

}