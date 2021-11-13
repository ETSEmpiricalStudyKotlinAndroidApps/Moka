package io.github.tonnyl.moka.data

import io.tonnyl.moka.graphql.fragment.RepositoryListItemFragment

data class RepositoryItem(

    /**
     * The description of the repository.
     */
    val description: String?,

    val id: String,

    /**
     * The name of the repository.
     */
    val name: String,

    /**
     * The repository's name with owner.
     */
    val nameWithOwner: String,

    /**
     * The User owner of the repository.
     */
    val owner: RepositoryOwner?,

    /**
     * The primary language of the repository's code.
     */
    val primaryLanguage: Language?,

    val forksCount: Int,

    val stargazersCount: Int

)

fun RepositoryListItemFragment.toNonNullRepositoryItem(): RepositoryItem {
    return RepositoryItem(
        description,
        id,
        name,
        nameWithOwner,
        repositoryOwner.repositoryOwner.toNonNullRepositoryOwner(),
        primaryLanguage?.language?.toNonNullLanguage(),
        forks.totalCount,
        stargazers.totalCount
    )
}