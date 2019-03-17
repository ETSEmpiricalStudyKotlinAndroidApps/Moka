package io.github.tonnyl.moka.ui.users

interface ItemUserActions {

    fun openProfile(login: String)

    fun followUserClicked(login: String, follow: Boolean)

}