package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import io.github.tonnyl.moka.fragment.User as RawUser

@Parcelize
data class User(

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
    val createdAt: Date,

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
    val resourcePath: Uri,

    /**
     * The user's description of what they're currently doing.
     */
    val status: UserStatus?,

    /**
     * Identifies the date and time when the object was last updated.
     */
    val updatedAt: Date,

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
    val viewerIsFollowing: Boolean,

    /**
     * A URL pointing to the user's public website/blog.
     */
    val websiteUrl: Uri?,

    val repositoriesTotalCount: Int,

    val followersTotalCount: Int,

    val followingTotalCount: Int,

    val starredRepositoriesTotalCount: Int,

    val projectsTotalCount: Int

) : Parcelable

fun RawUser.toNonNullUser(): User {
    return User(
        avatarUrl(),
        bio(),
        bioHTML(),
        company(),
        companyHTML(),
        createdAt(),
        email(),
        id(),
        isBountyHunter,
        isCampusExpert,
        isDeveloperProgramMember,
        isEmployee,
        isHireable,
        isSiteAdmin,
        isViewer,
        location(),
        login(),
        name(),
        resourcePath(),
        status()?.fragments()?.userStatus()?.toNonNullUserStatus(),
        updatedAt(),
        url(),
        viewerCanFollow(),
        viewerIsFollowing(),
        websiteUrl(),
        repositories().totalCount(),
        followers().totalCount(),
        following().totalCount(),
        starredRepositories().totalCount(),
        projects().totalCount()
    )
}