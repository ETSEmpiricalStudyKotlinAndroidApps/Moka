package io.github.tonnyl.moka.data

import io.tonnyl.moka.graphql.fragment.RepositoryListItemFragment

data class RepositoryItem(

    /**
     * The description of the repository.
     */
    val description: String?,

    /**
     * The description of the repository rendered to HTML.
     */
    val descriptionHTML: String,

    /**
     * The repository's URL.
     */
    val homepageUrl: String?,

    val id: String,

    /**
     * Indicates if the repository is unmaintained.
     */
    val isArchived: Boolean,

    /**
     * Identifies if the repository is a fork.
     */
    val isFork: Boolean,

    /**
     * Indicates if the repository has been locked or not.
     */
    val isLocked: Boolean,

    /**
     * Identifies if the repository is a mirror.
     */
    val isMirror: Boolean,

    /**
     * Identifies if the repository is private.
     */
    val isPrivate: Boolean,

    /**
     * The repository's original mirror URL.
     */
    val mirrorUrl: String?,

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

    val parent: ParentOrMirrorRepository?,

    /**
     * The primary language of the repository's code.
     */
    val primaryLanguage: Language?,

    /**
     * A description of the repository, rendered to HTML without any links in it.
     */
    val shortDescriptionHTML: String,

    /**
     * The HTTP URL for this repository.
     */
    val url: String,

    /**
     * Returns a boolean indicating whether the viewing user has starred this starrable.
     */
    val viewerHasStarred: Boolean,

    val forksCount: Int,

    val stargazersCount: Int

)

fun RepositoryListItemFragment.toNonNullRepositoryItem(): RepositoryItem {
    return RepositoryItem(
        description,
        descriptionHTML,
        homepageUrl,
        id,
        isArchived,
        isFork,
        isLocked,
        isMirror,
        isPrivate,
        mirrorUrl,
        name,
        nameWithOwner,
        repositoryOwner.repositoryOwner.toNonNullRepositoryOwner(),
        parent?.parentOfForkedRepository?.toNonNullParentOfForkedRepository(),
        primaryLanguage?.language?.toNonNullLanguage(),
        shortDescriptionHTML,
        url,
        viewerHasStarred,
        forks.totalCount,
        stargazers.totalCount
    )
}