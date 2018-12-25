package io.github.tonnyl.moka.ui.notifications

import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateUtils
import android.text.style.ForegroundColorSpan
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.data.NotificationReasons
import io.github.tonnyl.moka.net.GlideLoader
import io.github.tonnyl.moka.net.NetworkState
import io.github.tonnyl.moka.net.Status
import kotlinx.android.synthetic.main.item_network_state.view.*
import kotlinx.android.synthetic.main.item_notification.view.*

class NotificationAdapter(
        private val retryCallback: () -> Unit
) : PagedListAdapter<Notification, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private var networkState: NetworkState? = null

    var onItemClick: (Int, View) -> Unit = { _, _ ->

    }
    var onItemMenuSelected: (Int, View) -> Unit = { _, _ ->

    }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Notification>() {

            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_network_state -> NetworkStateViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_network_state, parent, false), retryCallback)
        R.layout.item_notification -> NotificationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false))
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
                    bind(item)
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

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {

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

        fun bind(notification: Notification) {
            with(itemView) {
                item_notification_repository_name.setTextFuture(PrecomputedTextCompat.getTextFuture(
                        notification.repository.fullName,
                        TextViewCompat.getTextMetricsParams(item_notification_repository_name),
                        null
                ))

                item_notification_time.setTextFuture(PrecomputedTextCompat.getTextFuture(
                        DateUtils.getRelativeTimeSpanString(notification.updatedAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                        TextViewCompat.getTextMetricsParams(item_notification_time),
                        null
                ))

                GlideLoader.loadAvatar(notification.repository.owner.avatarUrl, item_notification_repository_avatar)

                val notificationTypeResId: Int = when (notification.reason) {
                    NotificationReasons.ASSIGN.value -> R.string.notification_reason_assign
                    NotificationReasons.AUTHOR.value -> R.string.notification_reason_author
                    NotificationReasons.COMMENT.value -> R.string.notification_reason_comment
                    NotificationReasons.INVITATION.value -> R.string.notification_reason_invitation
                    NotificationReasons.MANUAL.value -> R.string.notification_reason_manual
                    NotificationReasons.MENTION.value -> R.string.notification_reason_mention
                    NotificationReasons.STATE_CHANGE.value -> R.string.notification_reason_state_change
                    NotificationReasons.SUBSCRIBED.value -> R.string.notification_reason_subscribed
                    // including NotificationReasons.TEAM_MENTION.value
                    else -> R.string.notification_reason_team_mention
                }
                val notificationReason = context.getString(notificationTypeResId)
                val notificationReasonPlusHyphen = context.getString(R.string.notification_caption_notification_type, notificationReason)
                val spannable = SpannableString(notificationReasonPlusHyphen + notification.subject.title)
                spannable.setSpan(ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.colorTextPrimary, null)), 0, notificationReasonPlusHyphen.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                item_notification_caption.setTextFuture(PrecomputedTextCompat.getTextFuture(
                        spannable,
                        TextViewCompat.getTextMetricsParams(item_notification_caption),
                        null
                ))
            }

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