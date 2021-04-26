package io.github.tonnyl.moka.data

import io.github.tonnyl.moka.queries.PullRequestQuery
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.Author.Companion.actor
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.BaseRef.Companion.ref
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.Editor.Companion.actor
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.HeadRef.Companion.ref
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.HeadRepositoryOwner.Companion.repositoryOwner
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.MergedBy.Companion.actor
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.Milestone.Companion.milestone
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.ReactionGroup.Companion.reactionGroup
import io.github.tonnyl.moka.type.*
import kotlinx.datetime.Instant

data class PullRequest(

    /**
     * Reason that the conversation was locked.
     */
    val activeLockReason: LockReason?,

    /**
     * The number of additions in this pull request.
     */
    val additions: Int,

    /**
     * The actor who authored the comment.
     */
    val author: Actor?,

    /**
     * Author's association with the subject of the comment.
     */
    val authorAssociation: CommentAuthorAssociation,

    /**
     * Identifies the base Ref associated with the pull request.
     */
    val baseRef: Ref?,

    /**
     * Identifies the name of the base Ref associated with the pull request, even if the ref has been deleted.
     */
    val baseRefName: String,

    /**
     * Identifies the oid of the base ref associated with the pull request, even if the ref has been deleted.
     */
    val baseRefOid: String,

    /**
     * The body as Markdown.
     */
    val body: String,

    /**
     * The body rendered to HTML.
     */
    val bodyHTML: String,

    /**
     * The body rendered to text.
     */
    val bodyText: String,

    /**
     * The number of changed files in this pull request.
     */
    val changedFiles: Int,

    /**
     * true if the pull request is closed.
     */
    val closed: Boolean,

    /**
     * Identifies the date and time when the object was closed.
     */
    val closedAt: Instant?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    /**
     * Check if this comment was created via an email reply.
     */
    val createdViaEmail: Boolean,

    /**
     * The number of deletions in this pull request.
     */
    val deletions: Int,

    /**
     * The actor who edited this pull request's body.
     */
    val editor: Actor?,

    /**
     * Identifies the head Ref associated with the pull request.
     */
    val headRef: Ref?,

    /**
     * Identifies the name of the head Ref associated with the pull request, even if the ref has been deleted.
     */
    val headRefName: String,

    /**
     * Identifies the oid of the head ref associated with the pull request, even if the ref has been deleted.
     */
    val headRefOid: String,

    /**
     * The owner of the repository associated with this pull request's head Ref.
     */
    val headRepositoryOwner: RepositoryOwner?,

    val id: String,

    /**
     * Check if this comment was edited and includes an edit with the creation data.
     */
    val includesCreatedEdit: Boolean,

    /**
     * The head and base repositories are different.
     */
    val isCrossRepository: Boolean,

    /**
     * The moment the editor made the last edit.
     */
    val lastEditedAt: Instant?,

    /**
     * true if the pull request is locked.
     */
    val locked: Boolean,

    /**
     * Indicates whether maintainers can modify the pull request.
     */
    val maintainerCanModify: Boolean,

    /**
     * Whether or not the pull request was merged.
     */
    val merged: Boolean,

    /**
     * The date and time that the pull request was merged.
     */
    val mergedAt: Instant?,

    /**
     * The actor who merged the pull request.
     */
    val mergedBy: Actor?,

    /**
     * Identifies the milestone associated with the pull request.
     */
    val milestone: Milestone?,

    /**
     * Identifies the pull request number.
     */
    val number: Int,

    /**
     * The permalink to the pull request.
     */
    val permalink: String,

    /**
     * Identifies when the comment was published at.
     */
    val publishedAt: Instant?,

    /**
     * A list of reactions grouped by content left on the subject.
     */
    var reactionGroups: MutableList<ReactionGroup>?,

    /**
     * The HTTP path for this pull request.
     */
    val resourcePath: String,

    /**
     * The HTTP path for reverting this pull request.
     */
    val revertResourcePath: String,

    /**
     * The HTTP URL for reverting this pull request.
     */
    val revertUrl: String,

    /**
     * Identifies the state of the pull request.
     */
    val state: PullRequestState,

    /**
     * Identifies the pull request title.
     */
    val title: String,

    /**
     * Identifies the date and time when the object was last updated.
     */
    val updatedAt: Instant,

    /**
     * The HTTP URL for this pull request.
     */
    val url: String,

    /**
     * Whether or not the viewer can apply suggestion.
     */
    val viewerCanApplySuggestion: Boolean,

    /**
     * Can user react to this subject.
     */
    val viewerCanReact: Boolean,

    /**
     * Check if the viewer is able to change their subscription status for the repository.
     */
    val viewerCanSubscribe: Boolean,

    /**
     * Check if the current viewer can update this object.
     */
    val viewerCanUpdate: Boolean,

    /**
     * Reasons why the current viewer can not update this comment.
     */
    val viewerCannotUpdateReasons: List<CommentCannotUpdateReason>,

    /**
     * Did the viewer author this comment.
     */
    val viewerDidAuthor: Boolean,

    /**
     * Identifies if the viewer is watching, not watching, or ignoring the subscribable entity.
     */
    val viewerSubscription: SubscriptionState?

)

fun PullRequestQuery.Data.Repository.PullRequest?.toNullablePullRequest(): PullRequest? {
    this ?: return null

    return PullRequest(
        activeLockReason,
        additions,
        author?.actor()?.toNonNullActor(),
        authorAssociation,
        baseRef?.ref()?.toNonNullRef(),
        baseRefName,
        baseRefOid,
        body,
        bodyHTML,
        bodyText,
        changedFiles,
        closed,
        closedAt,
        createdAt,
        createdViaEmail,
        deletions,
        editor?.actor()?.toNonNullActor(),
        headRef?.ref()?.toNonNullRef(),
        headRefName,
        headRefOid,
        headRepositoryOwner?.repositoryOwner()?.toNonNullRepositoryOwner(),
        id,
        includesCreatedEdit,
        isCrossRepository,
        lastEditedAt,
        locked,
        maintainerCanModify,
        merged,
        mergedAt,
        mergedBy?.actor()?.toNonNullActor(),
        milestone?.milestone()?.toNonNullMilestone(),
        number,
        permalink,
        publishedAt,
        reactionGroups?.filter {
            (it.reactionGroup()?.users?.totalCount ?: 0) > 0
        }?.mapNotNull {
            it.reactionGroup()?.toNonNullReactionGroup()
        }?.toMutableList(),
        resourcePath,
        revertResourcePath,
        revertUrl,
        state,
        title,
        updatedAt,
        url,
        viewerCanApplySuggestion,
        viewerCanReact,
        viewerCanSubscribe,
        viewerCanUpdate,
        viewerCannotUpdateReasons,
        viewerDidAuthor,
        viewerSubscription
    )
}