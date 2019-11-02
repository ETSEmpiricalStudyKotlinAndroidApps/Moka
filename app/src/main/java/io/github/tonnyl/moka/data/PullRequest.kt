package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.PullRequestQuery
import io.github.tonnyl.moka.type.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
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
    val closedAt: Date?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Date,

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
    val lastEditedAt: Date?,

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
    val mergedAt: Date?,

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
    val permalink: Uri,

    /**
     * Identifies when the comment was published at.
     */
    val publishedAt: Date?,

    /**
     * A list of reactions grouped by content left on the subject.
     */
    val reactionGroups: List<ReactionGroup?>?,

    /**
     * The HTTP path for this pull request.
     */
    val resourcePath: Uri,

    /**
     * The HTTP path for reverting this pull request.
     */
    val revertResourcePath: Uri,

    /**
     * The HTTP URL for reverting this pull request.
     */
    val revertUrl: Uri,

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
    val updatedAt: Date,

    /**
     * The HTTP URL for this pull request.
     */
    val url: Uri,

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

) : Parcelable

fun PullRequestQuery.PullRequest?.toNullablePullRequest(): PullRequest? {
    this ?: return null

    return PullRequest(
        activeLockReason,
        additions,
        author?.fragments?.actor?.toNonNullActor(),
        authorAssociation,
        baseRef?.fragments?.ref?.toNonNullRef(),
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
        editor?.fragments?.actor?.toNonNullActor(),
        headRef?.fragments?.ref?.toNonNullRef(),
        headRefName,
        headRefOid,
        headRepositoryOwner?.fragments?.repositoryOwner?.toNonNullRepositoryOwner(),
        id,
        includesCreatedEdit,
        isCrossRepository,
        lastEditedAt,
        locked,
        maintainerCanModify,
        merged,
        mergedAt,
        mergedBy?.fragments?.actor?.toNonNullActor(),
        milestone?.fragments?.milestone?.toNonNullMilestone(),
        number,
        permalink,
        publishedAt,
        reactionGroups?.map {
            it.fragments.reactionGroup.toNonNullReactionGroup()
        },
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