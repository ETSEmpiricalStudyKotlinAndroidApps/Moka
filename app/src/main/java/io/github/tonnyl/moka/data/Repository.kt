package io.github.tonnyl.moka.data

import android.net.Uri
import io.github.tonnyl.moka.queries.OrganizationsRepositoryQuery
import io.github.tonnyl.moka.queries.UsersRepositoryQuery
import io.github.tonnyl.moka.type.RepositoryLockReason
import io.github.tonnyl.moka.type.RepositoryPermission
import io.github.tonnyl.moka.type.SubscriptionState
import kotlinx.datetime.Instant

/**
 * A repository contains the content for a project.
 */
data class Repository(

    /**
     * Returns the code of conduct for this repository.
     */
    val codeOfConduct: CodeOfConduct?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

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
     * Identifies if the repository is a template that can be used to generate new repositories.
     */
    val isTemplate: Boolean,

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
     * The image used to represent this repository in Open Graph data.
     */
    val openGraphImageUrl: Uri,

    /**
     * The User owner of the repository.
     */
    val owner: RepositoryOwner,

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
    val pushedAt: Instant?,

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
    val updatedAt: Instant,

    /**
     * The HTTP URL for this repository.
     */
    val url: Uri,

    /**
     * Whether this repository has a custom image to use with Open Graph as opposed to being
     * represented by the owner's avatar.
     */
    val usesCustomOpenGraphImage: Boolean,

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

    val ownerName: String?,

    val isViewer: Boolean,

    val viewerIsFollowing: Boolean,

    val viewerCanFollow: Boolean,

    val forksCount: Int,

    val stargazersCount: Int,

    val issuesCount: Int,

    val pullRequestsCount: Int,

    val watchersCount: Int,

    val projectsCount: Int,

    val releasesCount: Int,

    val branchCount: Int,

    val topics: List<RepositoryTopic?>?

)

fun UsersRepositoryQuery.Data?.toNullableRepository(): Repository? {
    val user = this?.user ?: return null
    val repository = user.repository?.fragments?.repository ?: return null

    return Repository(
        repository.codeOfConduct?.fragments?.codeOfConduct?.toNonNullCodeOfConduct(),
        repository.createdAt,
        repository.defaultBranchRef?.fragments?.ref?.toNonNullRef(),
        repository.description,
        repository.descriptionHTML,
        repository.diskUsage,
        repository.forkCount,
        repository.hasIssuesEnabled,
        repository.hasWikiEnabled,
        repository.homepageUrl,
        repository.id,
        repository.isArchived,
        repository.isFork,
        repository.isLocked,
        repository.isMirror,
        repository.isPrivate,
        repository.isTemplate,
        repository.licenseInfo?.fragments?.license?.toNonNullLicense(),
        repository.lockReason,
        repository.mergeCommitAllowed,
        repository.mirrorUrl,
        repository.name,
        repository.nameWithOwner,
        repository.openGraphImageUrl,
        repository.owner.fragments.repositoryOwner.toNonNullRepositoryOwner(),
        repository.primaryLanguage?.fragments?.language?.toNonNullLanguage(),
        repository.projectsResourcePath,
        repository.projectsUrl,
        repository.pushedAt,
        repository.rebaseMergeAllowed,
        repository.resourcePath,
        repository.shortDescriptionHTML,
        repository.squashMergeAllowed,
        repository.sshUrl,
        repository.updatedAt,
        repository.url,
        repository.usesCustomOpenGraphImage,
        repository.viewerCanAdminister,
        repository.viewerCanCreateProjects,
        repository.viewerCanSubscribe,
        repository.viewerCanUpdateTopics,
        repository.viewerHasStarred,
        repository.viewerPermission,
        repository.viewerSubscription,
        user.name,
        user.isViewer,
        user.viewerIsFollowing,
        user.viewerCanFollow,
        repository.forks.totalCount,
        repository.stargazers.totalCount,
        repository.issues.totalCount,
        repository.pullRequests.totalCount,
        repository.watchers.totalCount,
        repository.projects.totalCount,
        repository.releases.totalCount,
        repository.refs?.totalCount ?: 0,
        repository.repositoryTopics.nodes?.map {
            it?.fragments?.repositoryTopic?.toNonNullRepositoryTopic()
        }
    )
}

fun OrganizationsRepositoryQuery.Data?.toNullableRepository(): Repository? {
    val organization = this?.organization ?: return null
    val repository = organization.repository?.fragments?.repository ?: return null

    return Repository(
        repository.codeOfConduct?.fragments?.codeOfConduct?.toNonNullCodeOfConduct(),
        repository.createdAt,
        repository.defaultBranchRef?.fragments?.ref?.toNonNullRef(),
        repository.description,
        repository.descriptionHTML,
        repository.diskUsage,
        repository.forkCount,
        repository.hasIssuesEnabled,
        repository.hasWikiEnabled,
        repository.homepageUrl,
        repository.id,
        repository.isArchived,
        repository.isFork,
        repository.isLocked,
        repository.isMirror,
        repository.isPrivate,
        repository.isTemplate,
        repository.licenseInfo?.fragments?.license?.toNonNullLicense(),
        repository.lockReason,
        repository.mergeCommitAllowed,
        repository.mirrorUrl,
        repository.name,
        repository.nameWithOwner,
        repository.openGraphImageUrl,
        repository.owner.fragments.repositoryOwner.toNonNullRepositoryOwner(),
        repository.primaryLanguage?.fragments?.language?.toNonNullLanguage(),
        repository.projectsResourcePath,
        repository.projectsUrl,
        repository.pushedAt,
        repository.rebaseMergeAllowed,
        repository.resourcePath,
        repository.shortDescriptionHTML,
        repository.squashMergeAllowed,
        repository.sshUrl,
        repository.updatedAt,
        repository.url,
        repository.usesCustomOpenGraphImage,
        repository.viewerCanAdminister,
        repository.viewerCanCreateProjects,
        repository.viewerCanSubscribe,
        repository.viewerCanUpdateTopics,
        repository.viewerHasStarred,
        repository.viewerPermission,
        repository.viewerSubscription,
        organization.name,
        isViewer = false,
        viewerIsFollowing = false,
        viewerCanFollow = false,
        forksCount = repository.forks.totalCount,
        stargazersCount = repository.stargazers.totalCount,
        issuesCount = repository.issues.totalCount,
        pullRequestsCount = repository.pullRequests.totalCount,
        watchersCount = repository.watchers.totalCount,
        projectsCount = repository.projects.totalCount,
        releasesCount = repository.releases.totalCount,
        branchCount = repository.refs?.totalCount ?: 0,
        topics = repository.repositoryTopics.nodes?.map {
            it?.fragments?.repositoryTopic?.toNonNullRepositoryTopic()
        }
    )
}