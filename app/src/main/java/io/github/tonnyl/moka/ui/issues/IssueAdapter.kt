package io.github.tonnyl.moka.ui.issues

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.net.GlideLoader
import kotlinx.android.synthetic.main.item_issue.view.*

class IssueAdapter : PagedListAdapter<IssueItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<IssueItem>() {

            override fun areItemsTheSame(oldItem: IssueItem, newItem: IssueItem): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: IssueItem, newItem: IssueItem): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = IssueViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_issue, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        with(holder.itemView) {
            issue_item_number.text = context.getString(R.string.issue_number, item.number)
            issue_item_author.text = context.getString(R.string.issue_created_by, item.login)
            issue_item_title.text = item.title
            issue_item_created_at.text = DateUtils.getRelativeTimeSpanString(item.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
            issue_item_status_image.setImageResource(if (item.closed) R.drawable.ic_issue_closed_24 else R.drawable.ic_issue_open_24)

            GlideLoader.loadAvatar(item.avatarUrl?.toString(), issue_item_avatar)
        }
    }

    class IssueViewHolder(view: View) : RecyclerView.ViewHolder(view)

}