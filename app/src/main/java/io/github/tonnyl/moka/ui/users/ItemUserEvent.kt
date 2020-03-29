package io.github.tonnyl.moka.ui.users

import io.github.tonnyl.moka.ui.profile.ProfileType

sealed class ItemUserEvent {

    data class ViewProfile(
        val login: String,
        val type: ProfileType
    ) : ItemUserEvent()

    data class FollowUser(val login: String) : ItemUserEvent()

}