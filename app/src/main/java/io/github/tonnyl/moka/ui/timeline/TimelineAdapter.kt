package io.github.tonnyl.moka.ui.timeline

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.GlideApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Event
import kotlinx.android.synthetic.main.item_event.view.*

class TimelineAdapter(val context: Context) : PagedListAdapter<Event, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private val body1TextStyle = TextAppearanceSpan(context, R.style.TextAppearance_MaterialComponents_Body2)

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Event>() {

            override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = EventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        with(holder.itemView) {
            GlideApp.with(context)
                    .load(item.actor.avatarUrl)
                    .circleCrop()
                    .into(event_user_avatar)

            val actionType = when (item.type) {
                Event.WATCH_EVENT -> context.getString(R.string.event_starred)
                Event.CREATE_EVENT -> context.getString(R.string.event_created)
                Event.FORK_EVENT -> context.getString(R.string.event_forked)
                Event.PUBLIC_EVENT -> context.getString(R.string.event_publicized)
                Event.ISSUES_EVENT -> context.getString(R.string.event_action_an_issue_in, item.payload.action)
                Event.PULL_REQUEST_EVENT -> context.getString(R.string.event_action_a_pr, item.payload.action)
                else -> ""
            }

            val builder = SpannableStringBuilder(item.actor.login)
                    .append(" $actionType ")
                    .append(item.repo.name)

            builder.setSpan(body1TextStyle, item.actor.login.length + 1, item.actor.login.length + 1 + actionType.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            event_action.setTextFuture(PrecomputedTextCompat.getTextFuture(
                    builder,
                    TextViewCompat.getTextMetricsParams(event_action),
                    null
            ))

            event_create_time.setTextFuture(PrecomputedTextCompat.getTextFuture(
                    DateUtils.getRelativeTimeSpanString(item.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                    TextViewCompat.getTextMetricsParams(event_create_time),
                    null
            ))
        }
    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view)

}