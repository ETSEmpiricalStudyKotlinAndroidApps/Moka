package io.github.tonnyl.moka.ui.repositories

sealed class RepositoryItemEvent {

    data class ViewRepository(
        val login: String,
        val repoName: String
    ) : RepositoryItemEvent()

    data class ViewProfile(val login: String) : RepositoryItemEvent()

    data class StarRepository(
        val login: String,
        val repoName: String
    ) : RepositoryItemEvent()

}