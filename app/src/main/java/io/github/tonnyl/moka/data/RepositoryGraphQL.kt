package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.RepositoryQuery
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * A repository contains the content for a project.
 */
@Parcelize
data class RepositoryGraphQL(
        val ownerId: String,
        val ownerAvatarUrl: Uri,
        val ownerName: String?,
        val ownerLogin: String,
        val viewerIsFollowing: Boolean,
        val viewerCanFollow: Boolean,
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
         * The User owner of the repository.
         */
        val owner: RepositoryOwnerGraphQL?,
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
         *
         * Argument: limit
         * Type: Int
         * Description: How many characters to return. The default value is 200.
         */
        val shortDescriptionHTML: String,
        /**
         * Whether or not squash-merging is enabled on this repository.
         */
        val squashMergeAllowed: Boolean,
        /**
         * The SSH URL to clone this repository.
         */
        val sshUrl: String,
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
        val projectsCount: Int
) : Parcelable {

    companion object {

        fun createFromRaw(data: RepositoryQuery.Data?): RepositoryGraphQL? {
            val user = data?.user()
            val repository = user?.repository()

            if (user == null || repository == null) {
                return null
            }
            return RepositoryGraphQL(
                    user.id(),
                    user.avatarUrl(),
                    user.name(),
                    user.login(),
                    user.viewerIsFollowing(),
                    user.viewerCanFollow(),
                    CodeOfConduct.createFromRaw(repository.codeOfConduct()),
                    repository.createdAt(),
                    repository.databaseId(),
                    Ref.createFromRaw(repository.defaultBranchRef()),
                    repository.description(),
                    repository.descriptionHTML(),
                    repository.diskUsage(),
                    repository.forkCount(),
                    repository.hasIssuesEnabled(),
                    repository.hasIssuesEnabled(),
                    repository.homepageUrl(),
                    repository.id(),
                    repository.isArchived,
                    repository.isFork,
                    repository.isLocked,
                    repository.isMirror,
                    repository.isPrivate,
                    License.createFromRaw(repository.licenseInfo()),
                    when (repository.lockReason()) {
                        io.github.tonnyl.moka.type.RepositoryLockReason.MOVING -> RepositoryLockReason.MOVING
                        io.github.tonnyl.moka.type.RepositoryLockReason.BILLING -> RepositoryLockReason.BILLING
                        io.github.tonnyl.moka.type.RepositoryLockReason.RENAME -> RepositoryLockReason.RENAME
                        io.github.tonnyl.moka.type.RepositoryLockReason.MIGRATING -> RepositoryLockReason.MIGRATING
                        else -> null
                    },
                    repository.mergeCommitAllowed(),
                    repository.mirrorUrl(),
                    repository.name(),
                    repository.nameWithOwner(),
                    RepositoryOwnerGraphQL.createFromRaw(repository.owner()),
                    Language.createFromRaw(repository.primaryLanguage()),
                    repository.projectsResourcePath(),
                    repository.projectsUrl(),
                    repository.pushedAt(),
                    repository.rebaseMergeAllowed(),
                    repository.resourcePath(),
                    repository.shortDescriptionHTML(),
                    repository.squashMergeAllowed(),
                    repository.sshUrl(),
                    repository.updatedAt(),
                    repository.url(),
                    repository.viewerCanAdminister(),
                    repository.viewerCanCreateProjects(),
                    repository.viewerCanSubscribe(),
                    repository.viewerCanUpdateTopics(),
                    repository.viewerHasStarred(),
                    when (repository.viewerPermission()) {
                        io.github.tonnyl.moka.type.RepositoryPermission.ADMIN -> RepositoryPermission.ADMIN
                        io.github.tonnyl.moka.type.RepositoryPermission.WRITE -> RepositoryPermission.WRITE
                        io.github.tonnyl.moka.type.RepositoryPermission.READ -> RepositoryPermission.READ
                        else -> null
                    },
                    when (repository.viewerSubscription()) {
                        io.github.tonnyl.moka.type.SubscriptionState.UNSUBSCRIBED -> SubscriptionState.UNSUBSCRIBED
                        io.github.tonnyl.moka.type.SubscriptionState.IGNORED -> SubscriptionState.IGNORED
                        io.github.tonnyl.moka.type.SubscriptionState.SUBSCRIBED -> SubscriptionState.SUBSCRIBED
                        else -> null
                    },
                    repository.forks().totalCount(),
                    repository.stargazers().totalCount(),
                    repository.issues().totalCount(),
                    repository.pullRequests().totalCount(),
                    repository.watchers().totalCount(),
                    repository.projects().totalCount()
            )
        }

    }

}