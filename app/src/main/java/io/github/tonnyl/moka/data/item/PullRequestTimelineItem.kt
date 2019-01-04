package io.github.tonnyl.moka.data.item

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.fragment.*
import io.github.tonnyl.moka.type.*
import kotlinx.android.parcel.Parcelize
import java.util.*

open class PullRequestTimelineItem {

    fun compare(other: PullRequestTimelineItem): Boolean {
        if (this::class != other::class) {
            return false
        }
        return when {
            (this is PullRequestAssignedEvent && other is PullRequestAssignedEvent)
                    || (this is PullRequestBaseRefForcePushedEvent && other is PullRequestBaseRefForcePushedEvent)
                    || (this is PullRequestClosedEvent && other is PullRequestClosedEvent)
                    || (this is PullRequestCommitEvent && other is PullRequestCommitEvent)
                    || (this is PullRequestCommitCommentThread && other is PullRequestCommitCommentThread)
                    || (this is PullRequestCrossReferencedEvent && other is PullRequestCrossReferencedEvent)
                    || (this is PullRequestDemilestonedEvent && other is PullRequestDemilestonedEvent)
                    || (this is PullRequestDeployedEvent && other is PullRequestDeployedEvent)
                    || (this is PullRequestDeploymentEnvironmentChangedEvent && other is PullRequestDeploymentEnvironmentChangedEvent)
                    || (this is PullRequestHeadRefDeletedEvent && other is PullRequestHeadRefDeletedEvent)
                    || (this is PullRequestHeadRefForcePushedEvent && other is PullRequestHeadRefForcePushedEvent)
                    || (this is PullRequestHeadRefRestoredEvent && other is PullRequestHeadRefRestoredEvent)
                    || (this is PullRequestIssueComment && other is PullRequestIssueComment)
                    || (this is PullRequestLabeledEvent && other is PullRequestLabeledEvent)
                    || (this is PullRequestLockedEvent && other is PullRequestLockedEvent)
                    || (this is PullRequestMergedEvent && other is PullRequestMergedEvent)
                    || (this is PullRequestMilestonedEvent && other is PullRequestMilestonedEvent)
                    || (this is PullRequestReview && other is PullRequestReview)
                    || (this is PullRequestReviewComment && other is PullRequestReviewComment)
                    || (this is PullRequestReviewThread && other is PullRequestReviewThread)
                    || (this is PullRequestReferencedEvent && other is PullRequestReferencedEvent)
                    || (this is PullRequestRenamedTitleEvent && other is PullRequestRenamedTitleEvent)
                    || (this is PullRequestReopenedEvent && other is PullRequestReopenedEvent)
                    || (this is PullRequestReviewDismissedEvent && other is PullRequestReviewDismissedEvent)
                    || (this is PullRequestReviewRequestRemovedEvent && other is PullRequestReviewRequestRemovedEvent)
                    || (this is PullRequestReviewRequestedEvent && other is PullRequestReviewRequestedEvent)
                    || (this is PullRequestUnassignedEvent && other is PullRequestUnassignedEvent)
                    || (this is PullRequestUnlabeledEvent && other is PullRequestUnlabeledEvent)
                    || (this is PullRequestUnlockedEvent && other is PullRequestUnlockedEvent) -> true
            else -> false
        }
    }

}

@Parcelize
data class PullRequestAssignedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         *
         * Argument: size
         * Type: Int
         * Description: The size of the resulting square image.
         */
        val actorAvatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val actorLogin: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        /**
         * The username used to login.
         */
        val assigneeLogin: String?,
        /**
         * The user's public profile name.
         */
        val assigneeName: String?
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: AssignedEventFragment?): PullRequestAssignedEvent? = if (data == null) null else PullRequestAssignedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.user()?.login(),
                data.user()?.name()
        )

    }

}

@Parcelize
data class PullRequestBaseRefForcePushedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         *
         * Argument: size
         * Type: Int
         * Description: The size of the resulting square image.
         */
        val actorAvatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val actorLogin: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        val beforeCommitOid: String?,
        val afterCommitOid: String?,
        val refName: String?
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: BaseRefForcePushedEventFragment?): PullRequestBaseRefForcePushedEvent? = if (data == null) null else PullRequestBaseRefForcePushedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.beforeCommit()?.oid(),
                data.afterCommit()?.oid(),
                data.ref()?.name()
        )

    }

}

/**
 * Represents a 'closed' event on any Closable.
 */
@Parcelize
data class PullRequestClosedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: ClosedEventFragment?): PullRequestClosedEvent? = if (data == null) null else PullRequestClosedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id()
        )

    }

}

@Parcelize
data class PullRequestCommitEvent(
        val authorAvatarUrl: Uri?,
        val authorLogin: String?,
        val committerAvatarUrl: Uri?,
        val committerLogin: String?,
        val message: String,
        val oid: String
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: CommitEventFragment?): PullRequestCommitEvent? = if (data == null) null else PullRequestCommitEvent(
                data.author()?.avatarUrl(),
                data.author()?.user()?.login(),
                data.committer()?.avatarUrl(),
                data.committer()?.user()?.login(),
                data.message(),
                data.oid()
        )

    }

}

@Parcelize
data class PullRequestCommitCommentThread(
        val oid: String,
        val position: Int?
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: CommitCommentThreadFragment?): PullRequestCommitCommentThread? = if (data == null) null else PullRequestCommitCommentThread(
                data.commit().oid(),
                data.position()
        )

    }

}

/**
 * Represents a mention made by one issue or pull request to another.
 */
@Parcelize
data class PullRequestCrossReferencedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        val issue: ReferencedEventIssueFragmentItem?,
        val pullRequest: ReferencedEventPullRequestFragmentItem?
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: CrossReferencedEventFragment?): PullRequestCrossReferencedEvent? = if (data == null) null else PullRequestCrossReferencedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                {
                    val fragment = data.source().fragments().referencedEventIssueFragment()
                    if (fragment == null) {
                        null
                    } else {
                        ReferencedEventIssueFragmentItem(fragment.title(), fragment.number())
                    }
                }(),
                {
                    val fragment = data.source().fragments().referencedEventPullRequestFragment()
                    if (fragment == null) {
                        null
                    } else {
                        ReferencedEventPullRequestFragmentItem(fragment.title(), fragment.number())
                    }
                }()
        )

    }

}

/**
 * Represents a 'demilestoned' event on a given issue or pull request.
 */
@Parcelize
data class PullRequestDemilestonedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        /**
         * Identifies the milestone title associated with the 'demilestoned' event.
         */
        val milestoneTitle: String
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: DemilestonedEventFragment?): PullRequestDemilestonedEvent? = if (data == null) null else PullRequestDemilestonedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.milestoneTitle()
        )

    }

}

@Parcelize
data class PullRequestDeployedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        val deploymentEnvironment: String?
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: DeployedEventFragment?): PullRequestDeployedEvent? = if (data == null) null else PullRequestDeployedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.deployment().environment()
        )

    }

}

@Parcelize
data class PullRequestDeploymentEnvironmentChangedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        val environment: String?,
        /**
         * Identifies the environment URL of the deployment.
         */
        val environmentUrl: Uri?,
        /**
         * Identifies the current state of the deployment.
         */
        val state: DeploymentStatusState,
        /**
         * Identifies the log URL of the deployment.
         */
        val logUrl: Uri?
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: DeploymentEnvironmentChangedEventFragment?): PullRequestDeploymentEnvironmentChangedEvent? = if (data == null) null else PullRequestDeploymentEnvironmentChangedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.deploymentStatus().deployment().environment(),
                data.deploymentStatus().environmentUrl(),
                data.deploymentStatus().state(),
                data.deploymentStatus().logUrl()
        )

    }

}

/**
 * Represents a 'head_ref_deleted' event on a given pull request.
 */
@Parcelize
data class PullRequestHeadRefDeletedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        /**
         * Identifies the name of the Ref associated with the head_ref_deleted event.
         */
        val headRefName: String
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: HeadRefDeletedEventFragment?): PullRequestHeadRefDeletedEvent? = if (data == null) null else PullRequestHeadRefDeletedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.headRefName()
        )

    }

}

/**
 * Represents a 'head_ref_force_pushed' event on a given pull request.
 */
@Parcelize
data class PullRequestHeadRefForcePushedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        /**
         * Identifies the after commit SHA for the 'head_ref_force_pushed' event.
         */
        val afterCommitOid: String?,
        /**
         * Identifies the before commit SHA for the 'head_ref_force_pushed' event.
         */
        val beforeCommitOid: String?,
        /**
         * The ref name.
         */
        val refName: String?
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: HeadRefForcePushedEventFragment?): PullRequestHeadRefForcePushedEvent? = if (data == null) null else PullRequestHeadRefForcePushedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.afterCommit()?.oid(),
                data.beforeCommit()?.oid(),
                data.ref()?.name()
        )

    }

}

/**
 * Represents a 'head_ref_restored' event on a given pull request.
 */
@Parcelize
data class PullRequestHeadRefRestoredEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        /**
         * Identifies the name of the base Ref associated with the pull request, even if the ref has been deleted.
         */
        val baseRefName: String,
        /**
         * Identifies the name of the head Ref associated with the pull request, even if the ref has been deleted.
         */
        val headRefName: String
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: HeadRefRestoredEventFragment?): PullRequestHeadRefRestoredEvent? = if (data == null) null else PullRequestHeadRefRestoredEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.pullRequest().baseRefName(),
                data.pullRequest().headRefName()
        )

    }

}

@Parcelize
data class PullRequestIssueComment(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val authorAvatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val authorLogin: String?,
        val authorAssociation: CommentAuthorAssociation,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        /**
         * The body as Markdown.
         */
        val body: String,
        /**
         * A URL pointing to the actor's public avatar.
         */
        val editorAvatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val editorLogin: String?
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: IssueCommentFragment?): PullRequestIssueComment? = if (data == null) null else PullRequestIssueComment(
                data.author()?.avatarUrl(),
                data.author()?.login(),
                data.authorAssociation(),
                data.createdAt(),
                data.body(),
                data.editor()?.avatarUrl(),
                data.editor()?.login()
        )

    }

}

/**
 * Represents a 'labeled' event on a given issue or pull request.
 */
@Parcelize
data class PullRequestLabeledEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        val labelName: String,
        val labelColor: String
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: LabeledEventFragment?): PullRequestLabeledEvent? = if (data == null) null else PullRequestLabeledEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.label().name(),
                data.label().color()
        )

    }

}

/**
 * Represents a 'locked' event on a given issue or pull request.
 */
@Parcelize
data class PullRequestLockedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        /**
         * Reason that the conversation was locked (optional).
         */
        val lockReason: LockReason?
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: LockedEventFragment?): PullRequestLockedEvent? = if (data == null) null else PullRequestLockedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.lockReason()
        )

    }

}

@Parcelize
data class PullRequestMergedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        /**
         * Identifies the name of the Ref associated with the merge event.
         */
        val mergeRefName: String,
        /**
         * ASCII-armored signature header from object.
         */
        val commitOid: String?
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: MergedEventFragment?): PullRequestMergedEvent? = if (data == null) null else PullRequestMergedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.mergeRefName(),
                data.commit()?.oid()
        )

    }

}

/**
 * Represents a 'milestoned' event on a given issue or pull request.
 */
@Parcelize
data class PullRequestMilestonedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        /**
         * Identifies the milestone title associated with the 'milestoned' event.
         */
        val milestoneTitle: String
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: MilestonedEventFragment?): PullRequestMilestonedEvent? = if (data == null) null else PullRequestMilestonedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.milestoneTitle()
        )

    }

}

@Parcelize
data class PullRequestReview(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        /**
         * Author's association with the subject of the comment.
         */
        val authorAssociation: CommentAuthorAssociation,
        /**
         * Identifies the pull request review body.
         */
        val body: String,
        /**
         * Identifies the current state of the pull request review.
         */
        val state: PullRequestReviewState,
        /**
         * Did the viewer author this comment.
         */
        val viewerDidAuthor: Boolean,
        /**
         * Reasons why the current viewer can not update this comment.
         */
        val viewerCannotUpdateReasons: List<CommentCannotUpdateReason>,
        /**
         * Check if the current viewer can delete this object.
         */
        val viewerCanDelete: Boolean,
        /**
         * Check if the current viewer can update this object.
         */
        val viewerCanUpdate: Boolean,
        val commentCount: Int
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: PullRequestReviewEventFragment?): PullRequestReview? = if (data == null) null else PullRequestReview(
                data.author()?.avatarUrl(),
                data.author()?.login(),
                data.createdAt(),
                data.id(),
                data.authorAssociation(),
                data.body(),
                data.state(),
                data.viewerDidAuthor(),
                data.viewerCannotUpdateReasons(),
                data.viewerCanDelete(),
                data.viewerCanUpdate(),
                data.comments().totalCount()
        )

    }

}

@Parcelize
data class PullRequestReviewComment(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val authorAvatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val authorLogin: String?,
        val createdAt: Date,
        val id: String,
        /**
         * Author's association with the subject of the comment.
         */
        val authorAssociation: CommentAuthorAssociation,
        val body: String,
        /**
         * Identifies when the comment body is outdated.
         */
        val outdated: Boolean
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: PullRequestReviewCommentFragment?): PullRequestReviewComment? = if (data == null) null else PullRequestReviewComment(
                data.author()?.avatarUrl(),
                data.author()?.login(),
                data.createdAt(),
                data.id(),
                data.authorAssociation(),
                data.body(),
                data.outdated()
        )

    }

}

@Parcelize
data class PullRequestReviewThread(
        val id: String
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: PullRequestReviewThreadFragment?): PullRequestReviewThread? = if (data == null) null else PullRequestReviewThread(
                data.id()
        )

    }

}

/**
 * Represents a 'referenced' event on a given ReferencedSubject.
 */
@Parcelize
data class PullRequestReferencedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        val issue: ReferencedEventIssueFragmentItem?,
        val pullRequest: ReferencedEventPullRequestFragmentItem?
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: ReferencedEventFragment?): PullRequestReferencedEvent? = if (data == null) null else PullRequestReferencedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                {
                    val fragment = data.subject().fragments().referencedEventIssueFragment()
                    if (fragment == null) {
                        null
                    } else {
                        ReferencedEventIssueFragmentItem(fragment.title(), fragment.number())
                    }
                }(),
                {
                    val fragment = data.subject().fragments().referencedEventPullRequestFragment()
                    if (fragment == null) {
                        null
                    } else {
                        ReferencedEventPullRequestFragmentItem(fragment.title(), fragment.number())
                    }
                }()
        )
    }

}

/**
 * Represents a 'renamed' event on a given issue or pull request
 */
@Parcelize
data class PullRequestRenamedTitleEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        /**
         * Identifies the current title of the issue or pull request.
         */
        val currentTitle: String,
        val id: String,
        /**
         * Identifies the previous title of the issue or pull request.
         */
        val previousTitle: String
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: RenamedTitleEventFragment?): PullRequestRenamedTitleEvent? = if (data == null) null else PullRequestRenamedTitleEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.currentTitle(),
                data.id(),
                data.previousTitle()
        )

    }

}

/**
 * Represents a 'reopened' event on any Closable.
 */
@Parcelize
data class PullRequestReopenedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: ReopenedEventFragment?): PullRequestReopenedEvent? = if (data == null) null else PullRequestReopenedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id()
        )

    }

}

/**
 * Represents a 'review_dismissed' event on a given issue or pull request.
 */
@Parcelize
data class PullRequestReviewDismissedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        /**
         * Identifies the message associated with the 'review_dismissed' event.
         */
        val message: String,
        /**
         * Identifies the previous state of the review with the 'review_dismissed' event.
         */
        val previousReviewState: PullRequestReviewState,
        val reviewAuthorLogin: String?
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: ReviewDismissedEventFragment?): PullRequestReviewDismissedEvent? = if (data == null) null else PullRequestReviewDismissedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.message(),
                data.previousReviewState(),
                data.review()?.author()?.login()
        )

    }

}

/**
 * Represents an 'review_request_removed' event on a given pull request.
 */
@Parcelize
data class PullRequestReviewRequestRemovedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        /**
         * Identifies the reviewer whose review request was removed.
         */
        val requestedReviewerTeamCombinedSlug: String?,
        /**
         * Identifies the reviewer whose review request was removed.
         */
        val requestedReviewerUserLogin: String?
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: ReviewRequestRemovedEventFragment?): PullRequestReviewRequestRemovedEvent? = if (data == null) null else PullRequestReviewRequestRemovedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.requestedReviewer()?.fragments()?.requestedReviewerTeamFragment()?.combinedSlug(),
                data.requestedReviewer()?.fragments()?.requestedReviewerUserFragment()?.login()
        )

    }

}

/**
 * Represents an 'review_requested' event on a given pull request.
 */
@Parcelize
data class PullRequestReviewRequestedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        /**
         * Identifies the reviewer whose review request was removed.
         */
        val requestedReviewerTeamCombinedSlug: String?,
        /**
         * Identifies the reviewer whose review request was removed.
         */
        val requestedReviewerUserLogin: String?
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: ReviewRequestedEventFragment?): PullRequestReviewRequestRemovedEvent? = if (data == null) null else PullRequestReviewRequestRemovedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.requestedReviewer()?.fragments()?.requestedReviewerTeamFragment()?.combinedSlug(),
                data.requestedReviewer()?.fragments()?.requestedReviewerUserFragment()?.login()
        )

    }

}

/**
 * Represents an 'unassigned' event on any assignable object.
 */
@Parcelize
data class PullRequestUnassignedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         *
         * Argument: size
         * Type: Int
         * Description: The size of the resulting square image.
         */
        val actorAvatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val actorLogin: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        /**
         * The username used to login.
         */
        val assigneeLogin: String?,
        /**
         * The user's public profile name.
         */
        val assigneeName: String?
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: UnassignedEventFragment?): PullRequestUnassignedEvent? = if (data == null) null else PullRequestUnassignedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.user()?.login(),
                data.user()?.name()
        )

    }

}

@Parcelize
data class PullRequestUnlabeledEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
        val id: String,
        val labelName: String,
        val labelColor: String
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: UnlabeledEventFragment?): PullRequestUnlabeledEvent? = if (data == null) null else PullRequestUnlabeledEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.label().name(),
                data.label().color()
        )

    }

}

/**
 * Represents an 'unlocked' event on a given issue or pull request.
 */
@Parcelize
data class PullRequestUnlockedEvent(
        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri?,
        /**
         * The username of the actor.
         */
        val login: String?,
        val createdAt: Date,
        /**
         * Identifies the date and time when the object was created.
         */
        val id: String
) : Parcelable, PullRequestTimelineItem() {

    companion object {

        fun createFromRaw(data: UnlockedEventFragment?): PullRequestUnlockedEvent? = if (data == null) null else PullRequestUnlockedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id()
        )

    }

}