package io.github.tonnyl.moka.ui.search.users

import io.github.tonnyl.moka.data.item.SearchedUserItem

sealed class SearchedUserItemEvent {

    data class ViewUserProfile(val login: String) : SearchedUserItemEvent()

    data class ViewOrganizationProfile(val login: String) : SearchedUserItemEvent()

    data class FollowUserEvent(val user: SearchedUserItem) : SearchedUserItemEvent()

}