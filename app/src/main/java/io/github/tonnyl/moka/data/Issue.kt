package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.queries.IssueQuery
import io.github.tonnyl.moka.type.*
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * An Issue is a place to discuss ideas, enhancements, tasks, and bugs for a project.
 */
@Parcelize
data class Issue(
    /**
     *Reason that the conversation was locked.
     */
    val activeLockReason: LockReason?,

    /**
     * The actor who authored the comment.
     */
    val author: Actor?,

    /**
     * Author's association with the subject of the comment.
     */
    val authorAssociation: CommentAuthorAssociation,

    /**
     * Identifies the body of the issue.
     */
    val body: String,

    /**
     * Identifies the body of the issue rendered to HTML.
     */
    val bodyHTML: String,

    /**
     * Identifies the body of the issue rendered to text.
     */
    val bodyText: String,

    /**
     * true if the object is closed (definition of closed may depend on type).
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
     * The actor who edited the comment.
     */
    val editor: Actor?,

    val id: String,

    /**
     * Check if this comment was edited and includes an edit with the creation data.
     */
    val includesCreatedEdit: Boolean,

    /**
     * The moment the editor made the last edit.
     */
    val lastEditedAt: Date?,

    /**
     * true if the object is locked.
     */
    val locked: Boolean,

    /**
     * Identifies the milestone associated with the issue.
     */
    val milestone: Milestone?,

    /**
     * Identifies the issue number.
     */
    val number: Int,

    /**
     * Identifies when the comment was published at.
     */
    val publishedAt: Date?,

    /**
     * A list of reactions grouped by content left on the subject.
     */
    var reactionGroups: MutableList<ReactionGroup>?,

    /**
     * The HTTP path for this issue.
     */
    val resourcePath: Uri,

    /**
     * Identifies the state of the issue.
     */
    val state: IssueState,

    /**
     * Identifies the issue title.
     */
    val title: String,

    /**
     * Identifies the date and time when the object was last updated.
     */
    val updatedAt: Date,

    /**
     * The HTTP URL for this issue.
     */
    val url: Uri,

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

fun IssueQuery.Issue.toNonNullIssue(): Issue {
    return Issue(
        activeLockReason,
        author?.fragments?.actor?.toNonNullActor(),
        authorAssociation,
        body,
        bodyHTML,
        bodyText,
        closed,
        closedAt,
        createdAt,
        createdViaEmail,
        editor?.fragments?.actor?.toNonNullActor(),
        id,
        includesCreatedEdit,
        lastEditedAt,
        locked,
        milestone?.fragments?.milestone?.toNonNullMilestone(),
        number,
        publishedAt,
        reactionGroups?.filter {
            it.fragments.reactionGroup.users.totalCount > 0
        }?.map {
            it.fragments.reactionGroup.toNonNullReactionGroup()
        }?.toMutableList(),
        resourcePath,
        state,
        title,
        updatedAt,
        url,
        viewerCanReact,
        viewerCanSubscribe,
        viewerCanUpdate,
        viewerCannotUpdateReasons,
        viewerDidAuthor,
        viewerSubscription
    )
}