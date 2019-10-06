package io.github.tonnyl.moka.data.item

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.data.*
import io.github.tonnyl.moka.fragment.OrganizationListItemFragment
import io.github.tonnyl.moka.fragment.RepositoryListItemFragment
import io.github.tonnyl.moka.fragment.UserListItemFragment
import kotlinx.android.parcel.Parcelize

interface SearchedUserOrOrgItem {

    val id: String

    fun areItemsTheSame(other: SearchedUserOrOrgItem): Boolean {
        if (this::class != other::class) {
            return false
        }

        return id == other.id
    }

    fun areContentsTheSame(other: SearchedUserOrOrgItem): Boolean {
        return this == other
    }

}

@Parcelize
data class SearchedUserItem(

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

    override val id: String,

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

) : Parcelable, SearchedUserOrOrgItem

fun UserListItemFragment.toNonNullSearchedUserItem(): SearchedUserItem {
    return SearchedUserItem(
        avatarUrl(),
        bio(),
        bioHTML(),
        id(),
        isViewer,
        login(),
        name(),
        url(),
        viewerCanFollow(),
        viewerIsFollowing()
    )
}

@Parcelize
data class SearchedOrganizationItem(

    /**
     * A URL pointing to the organization's public avatar.
     */
    val avatarUrl: Uri,

    /**
     * The organization's public profile description.
     */
    val description: String?,

    /**
     * The organization's public profile description rendered to HTML.
     */
    val descriptionHTML: String?,

    override val id: String,

    /**
     * Whether the organization has verified its profile email and website.
     */
    val isVerified: Boolean,

    /**
     * The organization's login name.
     */
    val login: String,

    /**
     * The organization's public profile name.
     */
    val name: String?,

    /**
     * The HTTP URL for this organization.
     */
    val url: Uri,

    /**
     * Viewer is an active member of this organization.
     */
    val viewerIsAMember: Boolean,

    /**
     * The organization's public profile URL.
     */
    val websiteUrl: Uri?

) : Parcelable, SearchedUserOrOrgItem

fun OrganizationListItemFragment.toNonNullSearchedOrganizationItem(): SearchedOrganizationItem {
    return SearchedOrganizationItem(
        avatarUrl(),
        description(),
        descriptionHTML(),
        id(),
        isVerified,
        login(),
        name(),
        url(),
        viewerIsAMember(),
        websiteUrl()
    )
}

@Parcelize
data class SearchedRepositoryItem(

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
    val homepageUrl: Uri?,

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
    val mirrorUrl: Uri?,

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
    val owner: RepositoryOwner,

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
    val url: Uri,

    /**
     * Returns a boolean indicating whether the viewing user has starred this starrable.
     */
    val viewerHasStarred: Boolean,

    val forksCount: Int,

    val stargazersCount: Int

) : Parcelable

fun RepositoryListItemFragment.toNonNullSearchedRepositoryItem(): SearchedRepositoryItem {
    return SearchedRepositoryItem(
        description(),
        descriptionHTML(),
        homepageUrl(),
        id(),
        isArchived,
        isFork,
        isLocked,
        isMirror,
        isPrivate,
        mirrorUrl(),
        name(),
        nameWithOwner(),
        owner().fragments().repositoryOwner().toNonNullRepositoryOwner(),
        parent()?.fragments()?.parentOfForkedRepository()?.toNonNullParentOfForkedRepository(),
        primaryLanguage()?.fragments()?.language()?.toNonNullLanguage(),
        shortDescriptionHTML(),
        url(),
        viewerHasStarred(),
        forks().totalCount(),
        stargazers().totalCount()
    )
}