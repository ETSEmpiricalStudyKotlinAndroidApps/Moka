package io.github.tonnyl.moka.ui.search.repositories

import io.github.tonnyl.moka.data.item.SearchedRepositoryItem

sealed class SearchedRepositoryItemEvent {

    data class ViewProfile(val login: String) : SearchedRepositoryItemEvent()

    data class ViewRepository(
        val login: String,
        val repoName: String
    ) : SearchedRepositoryItemEvent()

    data class StarRepository(val repo: SearchedRepositoryItem) : SearchedRepositoryItemEvent()

}