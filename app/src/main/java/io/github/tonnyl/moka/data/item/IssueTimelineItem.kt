package io.github.tonnyl.moka.data.item

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.data.CommentAuthorAssociation
import io.github.tonnyl.moka.data.LockReason
import io.github.tonnyl.moka.fragment.*
import kotlinx.android.parcel.Parcelize
import java.util.*
import io.github.tonnyl.moka.type.CommentAuthorAssociation as RawAuthorAssociation

open class IssueTimelineItem {

    fun areItemsTheSame(other: IssueTimelineItem): Boolean {
        if (this::class != other::class) {
            return false
        }
        return when {
            this is AssignedEvent
                    && other is AssignedEvent -> {
                this.id == other.id
            }
            this is ClosedEvent
                    && other is ClosedEvent -> {
                this.id == other.id
            }
            this is CommitEvent
                    && other is CommitEvent -> {
                this.oid == other.oid
            }
            this is CrossReferencedEvent
                    && other is CrossReferencedEvent -> {
                this.id == other.id
            }
            this is DemilestonedEvent
                    && other is DemilestonedEvent -> {
                this.id == other.id
            }
            this is IssueCommentEvent
                    && other is IssueCommentEvent -> {
                this.id == other.id
            }
            this is LabeledEvent
                    && other is LabeledEvent -> {
                this.id == other.id
            }
            this is LockedEvent
                    && other is LockedEvent -> {
                this.id == other.id
            }
            this is MilestonedEvent
                    && other is MilestonedEvent -> {
                this.id == other.id
            }
            this is ReferencedEvent
                    && other is ReferencedEvent -> {
                this.id == other.id
            }
            this is RenamedTitleEvent
                    && other is RenamedTitleEvent -> {
                this.id == other.id
            }
            this is ReopenedEvent
                    && other is ReopenedEvent -> {
                this.id == other.id
            }
            this is TransferredEvent
                    && other is TransferredEvent -> {
                this.id == other.id
            }
            this is UnassignedEvent
                    && other is UnassignedEvent -> {
                this.id == other.id
            }
            this is UnlabeledEvent
                    && other is UnlabeledEvent -> {
                this.id == other.id
            }
            this is UnlockedEvent
                    && other is UnlockedEvent -> {
                this.id == other.id
            }
            else -> false
        }
    }

    fun areContentsTheSame(other: IssueTimelineItem): Boolean {
        return when {
            this is AssignedEvent
                    && other is AssignedEvent -> {
                this == other
            }
            this is ClosedEvent
                    && other is ClosedEvent -> {
                this == other
            }
            this is CommitEvent
                    && other is CommitEvent -> {
                this == other
            }
            this is CrossReferencedEvent
                    && other is CrossReferencedEvent -> {
                this == other
            }
            this is DemilestonedEvent
                    && other is DemilestonedEvent -> {
                this == other
            }
            this is IssueCommentEvent
                    && other is IssueCommentEvent -> {
                this == other
            }
            this is LabeledEvent
                    && other is LabeledEvent -> {
                this == other
            }
            this is LockedEvent
                    && other is LockedEvent -> {
                this == other
            }
            this is MilestonedEvent
                    && other is MilestonedEvent -> {
                this == other
            }
            this is ReferencedEvent
                    && other is ReferencedEvent -> {
                this == other
            }
            this is RenamedTitleEvent
                    && other is RenamedTitleEvent -> {
                this == other
            }
            this is ReopenedEvent
                    && other is ReopenedEvent -> {
                this == other
            }
            this is TransferredEvent
                    && other is TransferredEvent -> {
                this == other
            }
            this is UnassignedEvent
                    && other is UnassignedEvent -> {
                this == other
            }
            this is UnlabeledEvent
                    && other is UnlabeledEvent -> {
                this == other
            }
            this is UnlockedEvent
                    && other is UnlockedEvent -> {
                this == other
            }
            else -> true
        }
    }

}

/**
 * Represents an 'assigned' event on any assignable object.
 */
@Parcelize
data class AssignedEvent(
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
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: AssignedEventFragment?): AssignedEvent? =
            if (data == null) null else AssignedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.user()?.login(),
                data.user()?.name()
            )

    }

}

/**
 * Represents a 'closed' event on any Closable.
 */
@Parcelize
data class ClosedEvent(
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
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: ClosedEventFragment?): ClosedEvent? =
            if (data == null) null else ClosedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id()
            )

    }

}

/**
 * Represents a Git commit.
 */
@Parcelize
data class CommitEvent(
    val authorAvatarUrl: Uri?,
    val authorLogin: String?,
    val committerAvatarUri: Uri?,
    val committerLogin: String?,
    val message: String,
    val oid: String
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: CommitEventFragment?): CommitEvent? =
            if (data == null) null else CommitEvent(
                data.author()?.avatarUrl(),
                data.author()?.user()?.login(),
                data.committer()?.avatarUrl(),
                data.committer()?.user()?.login(),
                data.message(),
                data.oid()
            )

    }

}

/**
 * Represents a mention made by one issue or pull request to another.
 */
@Parcelize
data class CrossReferencedEvent(
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
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: CrossReferencedEventFragment?): CrossReferencedEvent? =
            if (data == null) null else CrossReferencedEvent(
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
data class DemilestonedEvent(
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
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: DemilestonedEventFragment?): DemilestonedEvent? =
            if (data == null) null else DemilestonedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.milestoneTitle()
            )

    }

}

/**
 * Represents a comment on an Issue.
 */
@Parcelize
data class IssueCommentEvent(
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
    val id: String,
    /**
     * A URL pointing to the actor's public avatar.
     */
    val editorAvatarUrl: Uri?,
    /**
     * The username of the actor.
     */
    val editorLogin: String?
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: IssueCommentFragment?): IssueCommentEvent? =
            if (data == null) null else IssueCommentEvent(
                data.author()?.avatarUrl(),
                data.author()?.login(),
                when (data.authorAssociation()) {
                    RawAuthorAssociation.MEMBER -> CommentAuthorAssociation.MEMBER
                    RawAuthorAssociation.OWNER -> CommentAuthorAssociation.OWNER
                    RawAuthorAssociation.COLLABORATOR -> CommentAuthorAssociation.COLLABORATOR
                    RawAuthorAssociation.CONTRIBUTOR -> CommentAuthorAssociation.CONTRIBUTOR
                    RawAuthorAssociation.FIRST_TIME_CONTRIBUTOR -> CommentAuthorAssociation.FIRST_TIME_CONTRIBUTOR
                    RawAuthorAssociation.FIRST_TIMER -> CommentAuthorAssociation.FIRST_TIMER
                    RawAuthorAssociation.NONE -> CommentAuthorAssociation.NONE
                    RawAuthorAssociation.`$UNKNOWN` -> CommentAuthorAssociation.NONE
                },
                data.createdAt(),
                data.body(),
                data.id(),
                data.editor()?.avatarUrl(),
                data.editor()?.login()
            )

    }

}

/**
 * Represents a 'labeled' event on a given issue or pull request.
 */
@Parcelize
data class LabeledEvent(
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
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: LabeledEventFragment?): LabeledEvent? =
            if (data == null) null else LabeledEvent(
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
data class LockedEvent(
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
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: LockedEventFragment?): LockedEvent? =
            if (data == null) null else LockedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                when (data.lockReason()) {
                    io.github.tonnyl.moka.type.LockReason.OFF_TOPIC -> LockReason.OFF_TOPIC
                    io.github.tonnyl.moka.type.LockReason.TOO_HEATED -> LockReason.TOO_HEATED
                    io.github.tonnyl.moka.type.LockReason.RESOLVED -> LockReason.RESOLVED
                    // io.github.tonnyl.moka.type.LockReason.SPAM, io.github.tonnyl.moka.type.LockReason.`$UNKNOWN`
                    else -> LockReason.SPAM
                }
            )

    }

}

/**
 * Represents a 'milestoned' event on a given issue or pull request.
 */
@Parcelize
data class MilestonedEvent(
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
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: MilestonedEventFragment?): MilestonedEvent? =
            if (data == null) null else MilestonedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.milestoneTitle()
            )

    }

}

/**
 * Represents a 'referenced' event on a given ReferencedSubject.
 */
@Parcelize
data class ReferencedEvent(
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
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: ReferencedEventFragment?): ReferencedEvent? =
            if (data == null) null else ReferencedEvent(
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
data class RenamedTitleEvent(
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
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: RenamedTitleEventFragment?): RenamedTitleEvent? =
            if (data == null) null else RenamedTitleEvent(
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
data class ReopenedEvent(
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
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: ReopenedEventFragment?): ReopenedEvent? =
            if (data == null) null else ReopenedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id()
            )

    }

}

@Parcelize
data class TransferredEvent(
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
    val nameWithOwnerOfFromRepository: String?
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: TransferredEventFragment?): TransferredEvent? =
            if (data == null) null else TransferredEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id(),
                data.fromRepository()?.nameWithOwner()
            )

    }

}

/**
 * Represents an 'unassigned' event on any assignable object.
 */
@Parcelize
data class UnassignedEvent(
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
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: UnassignedEventFragment?): UnassignedEvent? =
            if (data == null) null else UnassignedEvent(
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
data class UnlabeledEvent(
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
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: UnlabeledEventFragment?): UnlabeledEvent? =
            if (data == null) null else UnlabeledEvent(
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
data class UnlockedEvent(
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
) : Parcelable, IssueTimelineItem() {

    companion object {

        fun createFromRaw(data: UnlockedEventFragment?): UnlockedEvent? =
            if (data == null) null else UnlockedEvent(
                data.actor()?.avatarUrl(),
                data.actor()?.login(),
                data.createdAt(),
                data.id()
            )

    }

}

@Parcelize
data class ReferencedEventIssueFragmentItem(
    val title: String,
    val number: Int
) : Parcelable {

    companion object {

        fun createFromRaw(data: ReferencedEventIssueFragment?): ReferencedEventIssueFragmentItem? =
            if (data == null) null else ReferencedEventIssueFragmentItem(
                data.title(),
                data.number()
            )

    }

}

@Parcelize
data class ReferencedEventPullRequestFragmentItem(
    val title: String,
    val number: Int
) : Parcelable {

    companion object {

        fun createFromRaw(data: ReferencedEventPullRequestFragment?): ReferencedEventPullRequestFragmentItem? =
            if (data == null) null else ReferencedEventPullRequestFragmentItem(
                data.title(),
                data.number()
            )

    }

}