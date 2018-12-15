package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.IssueQuery
import io.github.tonnyl.moka.type.CommentAuthorAssociation.*
import io.github.tonnyl.moka.type.CommentCannotUpdateReason
import io.github.tonnyl.moka.type.CommentCannotUpdateReason.*
import io.github.tonnyl.moka.type.IssueState.CLOSED
import io.github.tonnyl.moka.type.IssueState.OPEN
import io.github.tonnyl.moka.type.LockReason.*
import io.github.tonnyl.moka.type.SubscriptionState.*
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * An Issue is a place to discuss ideas, enhancements, tasks, and bugs for a project.
 */
@Parcelize
data class IssueGraphQL(
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
         * Identifies the primary key from the database.
         */
        val databaseId: Int?,

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
        val reactionGroups: List<ReactionGroupGraphQL>?,

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
) : Parcelable {

    companion object {

        fun createFromRaw(data: IssueQuery.Issue?): IssueGraphQL? = if (data == null) null else IssueGraphQL(
                when (data.activeLockReason()) {
                    OFF_TOPIC -> LockReason.OFF_TOPIC
                    TOO_HEATED -> LockReason.TOO_HEATED
                    RESOLVED -> LockReason.RESOLVED
                    SPAM -> LockReason.SPAM
                    // including io.github.tonnyl.moka.type.LockReason.`$UNKNOWN` and null
                    else -> null
                },
                Actor.createFromIssueAuthor(data.author()),
                when (data.authorAssociation()) {
                    MEMBER -> CommentAuthorAssociation.MEMBER
                    OWNER -> CommentAuthorAssociation.OWNER
                    COLLABORATOR -> CommentAuthorAssociation.COLLABORATOR
                    CONTRIBUTOR -> CommentAuthorAssociation.CONTRIBUTOR
                    FIRST_TIME_CONTRIBUTOR -> CommentAuthorAssociation.FIRST_TIME_CONTRIBUTOR
                    FIRST_TIMER -> CommentAuthorAssociation.FIRST_TIMER
                    // including [io.github.tonnyl.moka.type.CommentAuthorAssociation.`$UNKNOWN`], [io.github.tonnyl.moka.type.CommentAuthorAssociation.NONE]
                    else -> CommentAuthorAssociation.NONE
                },
                data.body(),
                data.bodyHTML(),
                data.bodyText(),
                data.closed(),
                data.closedAt(),
                data.createdAt(),
                data.createdViaEmail(),
                data.databaseId(),
                Actor.createFromIssueEditor(data.editor()),
                data.id(),
                data.includesCreatedEdit(),
                data.lastEditedAt(),
                data.locked(),
                Milestone.createFromIssueMilestone(data.milestone()),
                data.number(),
                data.publishedAt(),
                data.reactionGroups()?.map { ReactionGroupGraphQL.createFromIssueReactionGroup(it) },
                data.resourcePath(),
                when (data.state()) {
                    OPEN -> IssueState.OPEN
                    CLOSED -> IssueState.CLOSED
                    else -> IssueState.CLOSED
                },
                data.title(),
                data.updatedAt(),
                data.url(),
                data.viewerCanReact(),
                data.viewerCanSubscribe(),
                data.viewerCanUpdate(),
                data.viewerCannotUpdateReasons().map {
                    return@map when (it) {
                        VERIFIED_EMAIL_REQUIRED -> CommentCannotUpdateReason.VERIFIED_EMAIL_REQUIRED
                        INSUFFICIENT_ACCESS -> CommentCannotUpdateReason.INSUFFICIENT_ACCESS
                        LOCKED -> CommentCannotUpdateReason.LOCKED
                        LOGIN_REQUIRED -> CommentCannotUpdateReason.LOGIN_REQUIRED
                        MAINTENANCE -> CommentCannotUpdateReason.MAINTENANCE
                        else -> CommentCannotUpdateReason.VERIFIED_EMAIL_REQUIRED
                    }
                },
                data.viewerDidAuthor(),
                when (data.viewerSubscription()) {
                    UNSUBSCRIBED -> SubscriptionState.UNSUBSCRIBED
                    SUBSCRIBED -> SubscriptionState.SUBSCRIBED
                    IGNORED -> SubscriptionState.IGNORED
                    // including [io.github.tonnyl.moka.type.SubscriptionState.`$UNKNOWN`], null
                    else -> null
                }
        )

    }

}