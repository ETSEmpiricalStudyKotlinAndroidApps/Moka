package io.github.tonnyl.moka.ui.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.UserGraphQL
import io.github.tonnyl.moka.net.GlideLoader
import kotlinx.android.synthetic.main.item_user.view.*

class UserAdapter : PagedListAdapter<UserGraphQL, RecyclerView.ViewHolder>(DIFF_CALLBACK), View.OnClickListener {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserGraphQL>() {

            override fun areItemsTheSame(oldItem: UserGraphQL, newItem: UserGraphQL): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UserGraphQL, newItem: UserGraphQL): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        with(holder.itemView) {
            GlideLoader.loadAvatar(item.avatarUrl.toString(), item_user_avatar)

            item_user_name.text = item.name
            item_user_login.text = item.login
            item_user_description.text = item.bio
            item_user_follow_unfollow.setText(if (item.viewerIsFollowing) R.string.user_profile_unfollow else R.string.user_profile_follow)
        }
    }

    override fun onClick(v: View?) {

    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view)

}