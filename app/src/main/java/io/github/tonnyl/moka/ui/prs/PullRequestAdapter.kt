package io.github.tonnyl.moka.ui.prs

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.net.GlideLoader
import kotlinx.android.synthetic.main.item_issue_pr.view.*

class PullRequestAdapter : PagedListAdapter<PullRequestItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PullRequestItem>() {

            override fun areItemsTheSame(oldItem: PullRequestItem, newItem: PullRequestItem): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PullRequestItem, newItem: PullRequestItem): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = PullRequestViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_issue_pr, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        with(holder.itemView) {
            issue_pr_item_number.text = context.getString(R.string.issue_pr_number, item.number)
            issue_pr_item_author.text = context.getString(R.string.issue_pr_created_by, item.login)
            issue_pr_item_title.text = item.title
            issue_pr_item_created_at.text = DateUtils.getRelativeTimeSpanString(item.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
            issue_item_status_image.setImageResource(if (item.merged) R.drawable.ic_pr_merged else if (item.closed) R.drawable.ic_pr_closed else R.drawable.ic_pr_open)

            GlideLoader.loadAvatar(item.avatarUrl?.toString(), issue_pr_item_avatar)
        }
    }

    class PullRequestViewHolder(view: View) : RecyclerView.ViewHolder(view)

}