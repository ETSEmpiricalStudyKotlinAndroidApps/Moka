package io.github.tonnyl.moka.ui.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.FollowersQuery
import io.github.tonnyl.moka.FollowingQuery
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.net.GlideLoader
import kotlinx.android.synthetic.main.item_user.view.*

class UserAdapter(
        private val followersData: List<FollowersQuery.Node>?,
        private val followingData: List<FollowingQuery.Node>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val followerItem = followersData?.get(position)
        val followingItem = followingData?.get(position)

        with(holder.itemView) {
            if (followerItem != null) {
                GlideLoader.loadAvatar(followerItem.avatarUrl().toString(), item_user_avatar)

                item_user_name.text = followerItem.name()
                item_user_login.text = followerItem.login()
                item_user_description.text = followerItem.bio()
                item_user_follow_unfollow.setText(if (followerItem.viewerIsFollowing()) R.string.user_profile_unfollow else R.string.user_profile_follow)
            } else if (followingItem != null) {
                GlideLoader.loadAvatar(followingItem.avatarUrl().toString(), item_user_avatar)

                item_user_name.text = followingItem.name()
                item_user_login.text = followingItem.login()
                item_user_description.text = followingItem.bio()
                item_user_follow_unfollow.setText(if (followingItem.viewerIsFollowing()) R.string.user_profile_unfollow else R.string.user_profile_follow)
            }
        }
    }

    override fun getItemCount(): Int = followersData?.size ?: followingData?.size ?: 0

    override fun onClick(v: View?) {

    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view)

}