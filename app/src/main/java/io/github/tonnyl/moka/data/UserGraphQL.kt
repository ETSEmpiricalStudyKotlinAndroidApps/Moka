package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.FollowersQuery
import io.github.tonnyl.moka.FollowingQuery
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class UserGraphQL(
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
         * Identifies the primary key from the database.
         */
        val databaseId: Int?,

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

        val starredRepositoriesTotalCount: Int

) : Parcelable {

    companion object {

        fun createFromFollowerRaw(data: FollowersQuery.Node?): UserGraphQL? = if (data == null) null else UserGraphQL(
                data.avatarUrl(),
                data.bio(),
                data.bioHTML(),
                data.company(),
                data.companyHTML(),
                data.createdAt(),
                data.databaseId(),
                data.email(),
                data.id(),
                data.isBountyHunter,
                data.isCampusExpert,
                data.isDeveloperProgramMember,
                data.isEmployee,
                data.isHireable,
                data.isSiteAdmin,
                data.isViewer,
                data.location(),
                data.login(),
                data.name(),
                data.resourcePath(),
                data.updatedAt(),
                data.url(),
                data.viewerCanFollow(),
                data.viewerIsFollowing(),
                data.websiteUrl(),
                data.repositories().totalCount(),
                data.followers().totalCount(),
                data.following().totalCount(),
                data.starredRepositories().totalCount()
        )


        fun createFromFollowingRaw(data: FollowingQuery.Node?): UserGraphQL? = if (data == null) null else UserGraphQL(
                data.avatarUrl(),
                data.bio(),
                data.bioHTML(),
                data.company(),
                data.companyHTML(),
                data.createdAt(),
                data.databaseId(),
                data.email(),
                data.id(),
                data.isBountyHunter,
                data.isCampusExpert,
                data.isDeveloperProgramMember,
                data.isEmployee,
                data.isHireable,
                data.isSiteAdmin,
                data.isViewer,
                data.location(),
                data.login(),
                data.name(),
                data.resourcePath(),
                data.updatedAt(),
                data.url(),
                data.viewerCanFollow(),
                data.viewerIsFollowing(),
                data.websiteUrl(),
                data.repositories().totalCount(),
                data.followers().totalCount(),
                data.following().totalCount(),
                data.starredRepositories().totalCount()
        )

    }

}