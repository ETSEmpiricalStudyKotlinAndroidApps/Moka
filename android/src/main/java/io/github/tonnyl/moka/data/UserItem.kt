package io.github.tonnyl.moka.data

import io.tonnyl.moka.graphql.fragment.UserListItemFragment

data class UserItem(

    /**
     * A URL pointing to the user's public avatar.
     */
    val avatarUrl: String,

    /**
     * The user's public profile bio.
     */
    val bio: String?,

    val id: String,

    /**
     * The username used to login.
     */
    val login: String,

    /**
     * The user's public profile name.
     */
    val name: String?

)

fun UserListItemFragment.toNonNullUserItem(): UserItem {
    return UserItem(
        avatarUrl,
        bio,
        id,
        login,
        name
    )
}