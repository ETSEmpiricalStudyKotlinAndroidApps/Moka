package io.github.tonnyl.moka.data.item

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.data.*
import io.github.tonnyl.moka.fragment.OrgFragment
import io.github.tonnyl.moka.fragment.RepositoryFragment
import io.github.tonnyl.moka.fragment.UserFragment
import kotlinx.android.parcel.Parcelize
import java.util.*
import io.github.tonnyl.moka.type.RepositoryLockReason as RawRepositoryLockReason
import io.github.tonnyl.moka.type.RepositoryPermission as RawRepositoryPermission
import io.github.tonnyl.moka.type.SubscriptionState as RawSubscriptionState

open class SearchedUserOrOrgItem {

    fun areItemsTheSame(other: SearchedUserOrOrgItem): Boolean {
        if (this::class != other::class) {
            return false
        }

        return when {
            this is SearchedUserItem
                    && other is SearchedUserItem -> {
                this.id == other.id
            }
            this is SearchedOrganizationItem
                    && other is SearchedOrganizationItem -> {
                this.id == other.id
            }
            else -> false
        }
    }

    fun areContentsTheSame(other: SearchedUserOrOrgItem): Boolean {
        return when {
            this is SearchedUserItem
                    && other is SearchedUserItem -> {
                this == other
            }
            this is SearchedOrganizationItem
                    && other is SearchedOrganizationItem -> {
                this == other
            }
            else -> false
        }
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
         * The HTTP path listing user's projects.
         */
        val projectsResourcePath: Uri,

        /**
         * The HTTP URL listing user's projects.
         */
        val projectsUrl: Uri,

        /**
         * The HTTP path for this user
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
         * Can the current viewer create new projects on this owner.
         */
        val viewerCanCreateProjects: Boolean,

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
        val websiteUrl: Uri?

) : Parcelable, SearchedUserOrOrgItem() {

    companion object {

        fun createFromRaw(data: UserFragment?): SearchedUserItem? = if (data == null) null else SearchedUserItem(
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
                data.projectsResourcePath(),
                data.projectsUrl(),
                data.resourcePath(),
                UserStatus.createFromSearchedUserStatus(data.status()),
                data.updatedAt(),
                data.url(),
                data.viewerCanCreateProjects(),
                data.viewerCanFollow(),
                data.viewerIsFollowing(),
                data.websiteUrl()
        )

    }

}

@Parcelize
data class SearchedOrganizationItem(

        /**
         * A URL pointing to the organization's public avatar.
         */
        val avatarUrl: Uri,

        /**
         * Identifies the primary key from the database.
         */
        val databaseId: Int?,

        /**
         * The organization's public profile description.
         */
        val description: String?,

        /**
         * The organization's public email.
         */
        val email: String?,

        val id: String,

        /**
         * Whether the organization has verified its profile email and website.
         */
        val isVerified: Boolean,

        /**
         * The organization's public profile location.
         */
        val location: String?,

        /**
         * The organization's login name.
         */
        val login: String,

        /**
         * The organization's public profile name.
         */
        val name: String?,

        /**
         * The HTTP path creating a new team.
         */
        val newTeamResourcePath: Uri,

        /**
         * The HTTP URL creating a new team.
         */
        val newTeamUrl: Uri,

        /**
         * The HTTP path listing organization's projects.
         */
        val projectsResourcePath: Uri,

        /**
         * The HTTP URL listing organization's projects.
         */
        val projectsUrl: Uri,

        /**
         * The HTTP path for this organization.
         */
        val resourcePath: Uri,

        /**
         * The HTTP path listing organization's teams.
         */
        val teamsResourcePath: Uri,

        /**
         * The HTTP URL listing organization's teams.
         */
        val teamsUrl: Uri,

        /**
         * The HTTP URL for this organization.
         */
        val url: Uri,

        /**
         * Organization is adminable by the viewer.
         */
        val viewerCanAdminister: Boolean,

        /**
         * Can the current viewer create new projects on this owner.
         */
        val viewerCanCreateProjects: Boolean,

        /**
         * Viewer can create repositories on this organization
         */
        val viewerCanCreateRepositories: Boolean,

        /**
         * Viewer can create teams on this organization.
         */
        val viewerCanCreateTeams: Boolean,

        /**
         * Viewer is an active member of this organization.
         */
        val viewerIsAMember: Boolean,

        /**
         * The organization's public profile URL.
         */
        val websiteUrl: Uri?

) : Parcelable, SearchedUserOrOrgItem() {

    companion object {

        fun createFromRaw(data: OrgFragment?): SearchedOrganizationItem? = if (data == null) null else SearchedOrganizationItem(
                data.avatarUrl(),
                data.databaseId(),
                data.description(),
                data.publicEmail(),
                data.id(),
                data.isVerified,
                data.location(),
                data.login(),
                data.name(),
                data.newTeamResourcePath(),
                data.newTeamUrl(),
                data.projectsResourcePath(),
                data.projectsUrl(),
                data.resourcePath(),
                data.teamsResourcePath(),
                data.teamsUrl(),
                data.url(),
                data.viewerCanAdminister(),
                data.viewerCanCreateProjects(),
                data.viewerCanCreateRepositories(),
                data.viewerCanCreateTeams(),
                data.viewerIsAMember(),
                data.websiteUrl()
        )

    }

}

@Parcelize
data class SearchedRepositoryItem(

        /**
         * Returns the code of conduct for this repository.
         */
        val codeOfConduct: CodeOfConduct?,

        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,

        /**
         * Identifies the primary key from the database.
         */
        val databaseId: Int?,

        /**
         * The Ref associated with the repository's default branch.
         */
        val defaultBranchRef: Ref?,

        /**
         * The description of the repository.
         */
        val description: String?,

        /**
         * The description of the repository rendered to HTML.
         */
        val descriptionHTML: String,

        /**
         * The number of kilobytes this repository occupies on disk.
         */
        val diskUsage: Int?,

        /**
         * Returns how many forks there are of this repository in the whole network.
         */
        val forkCount: Int,

        /**
         * Indicates if the repository has issues feature enabled.
         */
        val hasIssuesEnabled: Boolean,

        /**
         * Indicates if the repository has wiki feature enabled.
         */
        val hasWikiEnabled: Boolean,

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
         * The license associated with the repository.
         */
        val licenseInfo: License?,

        /**
         * The reason the repository has been locked.
         */
        val lockReason: RepositoryLockReason?,

        /**
         * Whether or not PRs are merged with a merge commit on this repository.
         */
        val mergeCommitAllowed: Boolean,

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
         * A Git object in the repository.
         */
        val `object`: GitObject?,

        /**
         * The User owner of the repository.
         */
        val owner: RepositoryOwnerGraphQL,

        /**
         * The repository parent, if this is a fork.
         */
        val parentRepositoryWithOwner: String?,

        /**
         * The primary language of the repository's code.
         */
        val primaryLanguage: Language?,

        /**
         * The HTTP path listing the repository's projects.
         */
        val projectsResourcePath: Uri,

        /**
         * The HTTP URL listing the repository's projects.
         */
        val projectsUrl: Uri,

        /**
         * Identifies when the repository was last pushed to.
         */
        val pushedAt: Date?,

        /**
         * Whether or not rebase-merging is enabled on this repository.
         */
        val rebaseMergeAllowed: Boolean,

        /**
         * The HTTP path for this repository.
         */
        val resourcePath: Uri,

        /**
         * A description of the repository, rendered to HTML without any links in it.
         */
        val shortDescriptionHTML: String,

        /**
         * Whether or not squash-merging is enabled on this repository.
         */
        val squashMergeAllowed: Boolean,

        /**
         * The SSH URL to clone this repository.
         */
        val sshUrl: String?,

        /**
         * Identifies the date and time when the object was last updated.
         */
        val updatedAt: Date,

        /**
         * The HTTP URL for this repository.
         */
        val url: Uri,

        /**
         * Indicates whether the viewer has admin permissions on this repository.
         */
        val viewerCanAdminister: Boolean,

        /**
         * Can the current viewer create new projects on this owner.
         */
        val viewerCanCreateProjects: Boolean,

        /**
         * Check if the viewer is able to change their subscription status for the repository.
         */
        val viewerCanSubscribe: Boolean,

        /**
         * Indicates whether the viewer can update the topics of this repository.
         */
        val viewerCanUpdateTopics: Boolean,

        /**
         * Returns a boolean indicating whether the viewing user has starred this starrable.
         */
        val viewerHasStarred: Boolean,

        /**
         * The users permission level on the repository. Will return null if authenticated as an GitHub App.
         */
        val viewerPermission: RepositoryPermission?,

        /**
         * Identifies if the viewer is watching, not watching, or ignoring the subscribable entity.
         */
        val viewerSubscription: SubscriptionState?,

        val forksCount: Int,

        val stargazersCount: Int,

        val issuesCount: Int,

        val pullRequestsCount: Int,

        val watchersCount: Int,

        val projectsCount: Int,

        val releasesCount: Int,

        val branchCount: Int

) : Parcelable {

    companion object {

        fun createFromRaw(data: RepositoryFragment?): SearchedRepositoryItem? = if (data == null) null else SearchedRepositoryItem(
                CodeOfConduct.createFromRaw(data.codeOfConduct()),
                data.createdAt(),
                data.databaseId(),
                Ref.createFromRaw(data.defaultBranchRef()),
                data.description(),
                data.descriptionHTML(),
                data.diskUsage(),
                data.forkCount(),
                data.hasIssuesEnabled(),
                data.hasIssuesEnabled(),
                data.homepageUrl(),
                data.id(),
                data.isArchived,
                data.isFork,
                data.isLocked,
                data.isMirror,
                data.isPrivate,
                License.createFromRaw(data.licenseInfo()),
                when (data.lockReason()) {
                    RawRepositoryLockReason.MOVING -> RepositoryLockReason.MOVING
                    RawRepositoryLockReason.BILLING -> RepositoryLockReason.BILLING
                    RawRepositoryLockReason.RENAME -> RepositoryLockReason.RENAME
                    RawRepositoryLockReason.MIGRATING -> RepositoryLockReason.MIGRATING
                    // including RawRepositoryLockReason.`$UNKNOWN` or null
                    else -> null
                },
                data.mergeCommitAllowed(),
                data.mirrorUrl(),
                data.name(),
                data.nameWithOwner(),
                GitObject.createFromRaw(data.`object`()),
                RepositoryOwnerGraphQL.createFromRaw(data.owner()),
                data.parent()?.nameWithOwner(),
                Language.createFromRaw(data.primaryLanguage()),
                data.projectsResourcePath(),
                data.projectsUrl(),
                data.pushedAt(),
                data.rebaseMergeAllowed(),
                data.resourcePath(),
                data.shortDescriptionHTML(),
                data.squashMergeAllowed(),
                data.sshUrl(),
                data.updatedAt(),
                data.url(),
                data.viewerCanAdminister(),
                data.viewerCanCreateProjects(),
                data.viewerCanSubscribe(),
                data.viewerCanUpdateTopics(),
                data.viewerHasStarred(),
                when (data.viewerPermission()) {
                    RawRepositoryPermission.ADMIN -> RepositoryPermission.ADMIN
                    RawRepositoryPermission.WRITE -> RepositoryPermission.WRITE
                    RawRepositoryPermission.READ -> RepositoryPermission.READ
                    // including RawRepositoryPermission.`$UNKNOWN` and null
                    else -> null
                },
                when (data.viewerSubscription()) {
                    RawSubscriptionState.UNSUBSCRIBED -> SubscriptionState.UNSUBSCRIBED
                    RawSubscriptionState.SUBSCRIBED -> SubscriptionState.SUBSCRIBED
                    RawSubscriptionState.IGNORED -> SubscriptionState.IGNORED
                    // including RawSubscriptionState.`$UNKNOWN` and null
                    else -> null
                },
                data.forkCount(),
                data.stargazers().totalCount(),
                data.issues().totalCount(),
                data.pullRequests().totalCount(),
                data.watchers().totalCount(),
                data.projects().totalCount(),
                data.releases().totalCount(),
                data.refs()?.totalCount() ?: 0
        )

    }

}