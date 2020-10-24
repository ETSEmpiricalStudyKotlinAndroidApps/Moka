package io.github.tonnyl.moka.data

import android.net.Uri
import io.github.tonnyl.moka.fragment.UserListItemFragment

data class UserItem(

    /**
     * A URL pointing to the user's public avatar.
     */
    val avatarUrl: Uri,

    /**
     * The user's public profile bio.
     */
    val bio: String?,

    /**
     * The user's public profile bio as HTML.
     */
    val bioHTML: String,

    val id: String,

    /**
     * Whether or not this user is the viewing user.
     */
    val isViewer: Boolean,

    /**
     * The username used to login.
     */
    val login: String,

    /**
     * The user's public profile name.
     */
    val name: String?,

    /**
     * The HTTP URL for this user.
     */
    val url: Uri,

    /**
     * Whether or not the viewer is able to follow the user.
     */
    val viewerCanFollow: Boolean,

    /**
     * Whether or not this user is followed by the viewer.
     */
    val viewerIsFollowing: Boolean

)

fun UserListItemFragment.toNonNullUserItem(): UserItem {
    return UserItem(
        avatarUrl,
        bio,
        bioHTML,
        id,
        isViewer,
        login,
        name,
        url,
        viewerCanFollow,
        viewerIsFollowing
    )
}