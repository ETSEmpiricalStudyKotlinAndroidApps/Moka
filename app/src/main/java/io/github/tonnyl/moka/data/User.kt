package io.github.tonnyl.moka.data

import io.github.tonnyl.moka.fragment.PinnableItem.Companion.asGist
import io.github.tonnyl.moka.fragment.PinnableItem.Companion.repositoryListItemFragment
import io.github.tonnyl.moka.fragment.User.PinnedItems.Node.Companion.pinnableItem
import io.github.tonnyl.moka.fragment.User.Status.Companion.userStatus
import kotlinx.datetime.Instant
import io.github.tonnyl.moka.fragment.User as RawUser

data class User(

    /**
     * A URL pointing to the user's public avatar.
     */
    val avatarUrl: String,

    /**
     * The user's public profile bio.
     */
    val bio: String?,

    /**
     * The user's public profile bio as HTML.
     */
    val bioHTML: String,

    /**
     * The user's public profile company.
     */
    val company: String?,

    /**
     * The user's public profile company as HTML.
     */
    val companyHTML: String,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    /**
     * The user's publicly visible profile email.
     */
    val email: String,

    val id: String,

    /**
     * Whether or not this user is a participant in the GitHub Security Bug Bounty.
     */
    val isBountyHunter: Boolean,

    /**
     * Whether or not this user is a participant in the GitHub Campus Experts Program.
     */
    val isCampusExpert: Boolean,

    /**
     * Whether or not this user is a GitHub Developer Program member.
     */
    val isDeveloperProgramMember: Boolean,

    /**
     * Whether or not this user is a GitHub employee.
     */
    val isEmployee: Boolean,

    /**
     * Whether or not the user has marked themselves as for hire.
     */
    val isHireable: Boolean,

    /**
     * Whether or not this user is a site administrator.
     */
    val isSiteAdmin: Boolean,

    /**
     * Whether or not this user is the viewing user.
     */
    val isViewer: Boolean,

    /**
     * The user's public profile location.
     */
    val location: String?,

    /**
     * The username used to login.
     */
    val login: String,

    /**
     * The user's public profile name.
     */
    val name: String?,

    /**
     * The HTTP path for this user.
     */
    val resourcePath: String,

    /**
     * The user's description of what they're currently doing.
     */
    val status: UserStatus?,

    /**
     * Identifies the date and time when the object was last updated.
     */
    val updatedAt: Instant,

    /**
     * The HTTP URL for this user.
     */
    val url: String,

    /**
     * The user's Twitter username.
     */
    val twitterUsername: String?,

    /**
     * Whether or not the viewer is able to follow the user.
     */
    val viewerCanFollow: Boolean,

    /**
     * Whether or not this user is followed by the viewer.
     */
    val viewerIsFollowing: Boolean,

    /**
     * A URL pointing to the user's public website/blog.
     */
    val websiteUrl: String?,

    val repositoriesTotalCount: Int,

    val followersTotalCount: Int,

    val followingTotalCount: Int,

    val starredRepositoriesTotalCount: Int,

    val projectsTotalCount: Int,

    val pinnedItems: MutableList<PinnableItem>?

)

fun RawUser.toNonNullUser(): User {
    val pinnableItems = mutableListOf<PinnableItem>()
    pinnedItems.nodes?.map { node ->
        node?.pinnableItem()?.let { fragment ->
            fragment.asGist()?.let {
                pinnableItems.add(it.toGist())
            } ?: fragment.repositoryListItemFragment()?.let {
                pinnableItems.add(it.toNonNullRepositoryItem())
            }
        }
    }

    return User(
        avatarUrl,
        bio,
        bioHTML,
        company,
        companyHTML,
        createdAt,
        email,
        id,
        isBountyHunter,
        isCampusExpert,
        isDeveloperProgramMember,
        isEmployee,
        isHireable,
        isSiteAdmin,
        isViewer,
        location,
        login,
        name,
        resourcePath,
        status?.userStatus()?.toNonNullUserStatus(),
        updatedAt,
        url,
        twitterUsername,
        viewerCanFollow,
        viewerIsFollowing,
        websiteUrl,
        repositories.totalCount,
        followers.totalCount,
        following.totalCount,
        starredRepositories.totalCount,
        projects.totalCount,
        pinnableItems
    )
}