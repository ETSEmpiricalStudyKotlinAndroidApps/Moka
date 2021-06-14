package io.github.tonnyl.moka.data

import io.github.tonnyl.moka.data.Repository.Companion.MAX_LANGUAGE_DISPLAY_COUNT
import io.github.tonnyl.moka.fragment.Ref.Target.Companion.asCommit
import io.github.tonnyl.moka.fragment.Repository.CodeOfConduct.Companion.codeOfConduct
import io.github.tonnyl.moka.fragment.Repository.DefaultBranchRef.Companion.ref
import io.github.tonnyl.moka.fragment.Repository.LicenseInfo.Companion.license
import io.github.tonnyl.moka.fragment.Repository.Owner.Companion.repositoryOwner
import io.github.tonnyl.moka.fragment.Repository.PrimaryLanguage.Companion.language
import io.github.tonnyl.moka.fragment.Repository.RepositoryTopics.Node.Companion.repositoryTopic
import io.github.tonnyl.moka.queries.OrganizationsRepositoryQuery
import io.github.tonnyl.moka.queries.OrganizationsRepositoryQuery.Data.Organization.Repository.Companion.repository
import io.github.tonnyl.moka.queries.UsersRepositoryQuery
import io.github.tonnyl.moka.queries.UsersRepositoryQuery.Data.User.Repository.Companion.repository
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
     * The image used to represent this repository in Open Graph data.
     */
    val openGraphImageUrl: String,

    /**
     * The User owner of the repository.
     */
    val owner: RepositoryOwner?,

    /**
     * The primary language of the repository's code.
     */
    val primaryLanguage: Language?,

    /**
     * The total size in bytes of files written in that language.
     */
    val languagesTotalSize: Int?,

    /**
     * A list of languages associated with the parent.
     */
    val languages: List<Language>?,

    /**
     * Represents the language of a repository.
     */
    val languageEdges: List<Int>?,

    /**
     * Computed field, if the languages count is more than [MAX_LANGUAGE_DISPLAY_COUNT], this field
     * will represent the remaining languages percentage, or null.
     */
    val otherLanguagePercentage: Double?,

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
    val resourcePath: String,

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
    val url: String,

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

    val releasesCount: Int,

    val branchCount: Int,

    val commitsCount: Int,

    val topics: List<RepositoryTopic?>?

) {

    companion object {

        const val MAX_LANGUAGE_DISPLAY_COUNT = 20

    }

}

fun UsersRepositoryQuery.Data?.toNullableRepository(): Repository? {
    val user = this?.user ?: return null
    val repository = user.repository?.repository() ?: return null

    return Repository(
        repository.codeOfConduct?.codeOfConduct()?.toNonNullCodeOfConduct(),
        repository.createdAt,
        repository.defaultBranchRef?.ref()?.toNonNullRef(),
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
        repository.licenseInfo?.license()?.toNonNullLicense(),
        repository.lockReason,
        repository.mergeCommitAllowed,
        repository.mirrorUrl,
        repository.name,
        repository.nameWithOwner,
        repository.openGraphImageUrl,
        repository.owner.repositoryOwner()?.toNonNullRepositoryOwner(),
        repository.primaryLanguage?.language()?.toNonNullLanguage(),
        repository.languages?.totalSize,
        repository.languages?.nodes?.mapNotNull {
            it?.toNonNullLanguage()
        },
        repository.languages?.edges?.mapNotNull {
            it?.size
        },
        if (repository.languages?.nodes.orEmpty().size > 20) {
            (repository.languages?.edges?.sumOf {
                (it?.size ?: 0).toDouble()
            } ?: 0).toDouble() / (repository.languages?.totalSize ?: 1).toDouble()
        } else {
            null
        },
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
        repository.releases.totalCount,
        repository.refs?.totalCount ?: 0,
        repository.defaultBranchRef?.ref()?.target?.asCommit()?.history?.totalCount ?: 0,
        repository.repositoryTopics.nodes?.map {
            it?.repositoryTopic()?.toNonNullRepositoryTopic()
        }
    )
}

fun OrganizationsRepositoryQuery.Data?.toNullableRepository(): Repository? {
    val organization = this?.organization ?: return null
    val repository = organization.repository?.repository() ?: return null

    return Repository(
        repository.codeOfConduct?.codeOfConduct()?.toNonNullCodeOfConduct(),
        repository.createdAt,
        repository.defaultBranchRef?.ref()?.toNonNullRef(),
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
        repository.licenseInfo?.license()?.toNonNullLicense(),
        repository.lockReason,
        repository.mergeCommitAllowed,
        repository.mirrorUrl,
        repository.name,
        repository.nameWithOwner,
        repository.openGraphImageUrl,
        repository.owner.repositoryOwner()?.toNonNullRepositoryOwner(),
        repository.primaryLanguage?.language()?.toNonNullLanguage(),
        repository.languages?.totalSize,
        repository.languages?.nodes?.mapNotNull {
            it?.toNonNullLanguage()
        },
        repository.languages?.edges?.mapNotNull {
            it?.size
        },
        if (repository.languages?.nodes.orEmpty().size > 20) {
            (repository.languages?.edges?.sumOf {
                (it?.size ?: 0).toDouble()
            } ?: 0).toDouble() / (repository.languages?.totalSize ?: 1).toDouble()
        } else {
            null
        },
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
        releasesCount = repository.releases.totalCount,
        branchCount = repository.refs?.totalCount ?: 0,
        commitsCount = repository.defaultBranchRef?.ref()?.target?.asCommit()?.history?.totalCount
            ?: 0,
        topics = repository.repositoryTopics.nodes?.map {
            it?.repositoryTopic()?.toNonNullRepositoryTopic()
        }
    )
}