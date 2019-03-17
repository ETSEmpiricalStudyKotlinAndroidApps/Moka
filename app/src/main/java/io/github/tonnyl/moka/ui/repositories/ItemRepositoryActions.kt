package io.github.tonnyl.moka.ui.repositories

interface ItemRepositoryActions {

    fun openRepository(login: String, repositoryName: String)

    fun openProfile(login: String)

    fun starRepositoryClicked(repositoryNameWithOwner: String, star: Boolean)

}