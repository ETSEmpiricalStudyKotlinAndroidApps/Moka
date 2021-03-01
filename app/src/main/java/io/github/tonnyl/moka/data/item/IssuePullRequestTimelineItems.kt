package io.github.tonnyl.moka.data.item

import android.net.Uri
import androidx.annotation.WorkerThread
import io.github.tonnyl.moka.data.Actor
import io.github.tonnyl.moka.data.ReactionGroup
import io.github.tonnyl.moka.data.toNonNullActor
import io.github.tonnyl.moka.data.toNonNullReactionGroup
import io.github.tonnyl.moka.fragment.*
import io.github.tonnyl.moka.type.*
import io.github.tonnyl.moka.util.HtmlHandler
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.datetime.Instant
import io.github.tonnyl.moka.fragment.PullRequestTimelineItemPullRequest as RawPullRequestTimelineItemPullRequest

// ===== base issue/pull request timeline item definition =====
interface TimelineItem {

    val id: String

}

interface IssueTimelineItem : TimelineItem

interface PullRequestTimelineItem : TimelineItem
// ===== base issue/pull request timeline item definition =====

// ===== base issue/pull request common definition =====
data class Bot(

    /**
     * A URL pointing to the GitHub App's public avatar.
     */
    val avatarUrl: Uri,

    /**
     * The username of the actor.
     */
    val login: String,

    /**
     * The HTTP URL for this bot
     */
    val url: Uri

)

fun IssuePullRequestTimelineItemBotFragment.toNonNullBot(): Bot {
    return Bot(avatarUrl, login, url)
}

data class Mannequin(

    /**
     * A URL pointing to the GitHub App's public avatar.
     */
    val avatarUrl: Uri,

    val id: String,

    /**
     * The username of the actor.
     */
    val login: String,

    /**
     * The URL to this resource.
     */
    val url: Uri

)

fun IssuePullRequestTimelineItemMannequinFragment.toNonNullMannequin(): Mannequin {
    return Mannequin(avatarUrl, id, login, url)
}

data class Organization(

    /**
     * A URL pointing to the organization's public avatar.
     */
    val avatarUrl: Uri,

    val id: String,

    /**
     * The organization's login name.
     */
    val login: String,

    /**
     * The organization's public profile name.
     */
    val name: String?,

    /**
     * The HTTP URL for this organization.
     */
    val url: Uri

)

fun IssuePullRequestTimelineItemOrganizationFragment.toNonNullOrganization(): Organization {
    return Organization(avatarUrl, id, login, name, url)
}

data class User(
    /**
     * A URL pointing to the user's public avatar.
     */
    val avatarUrl: Uri,

    val id: String,

    /**
     * The username used to login.
     */
    val login: String,

    /**
     * The user's public profile name.
     */
    val name: String?,

    /**
     * The HTTP URL for this user
     */
    val url: Uri

)

fun IssuePullRequestTimelineItemUserFragment.toNonNullUser(): User {
    return User(avatarUrl, id, login, name, url)
}

data class Team(

    /**
     * A URL pointing to the team's avatar.
     */
    val avatarUrl: Uri?,

    /**
     * The slug corresponding to the organization and team.
     */
    val combinedSlug: String,

    /**
     * The name of the team.
     */
    val name: String,

    /**
     * The HTTP URL for this team
     */
    val url: Uri,

    val id: String

)

fun IssuePullRequestTimelineItemTeamFragment.toNonNullTeam(): Team {
    return Team(avatarUrl, combinedSlug, name, url, id)
}

data class MilestoneItemIssue(

    /**
     * Identifies the issue title.
     */
    val title: String,

    /**
     * Identifies the issue number.
     */
    val number: Int,

    val id: String,

    /**
     * The HTTP URL for this issue
     */
    val url: Uri

)

fun MilestoneItemIssueFragment.toNonNullMilestoneItemIssue(): MilestoneItemIssue {
    return MilestoneItemIssue(title, number, id, url)
}

/**
 * Object referenced by event.
 */
data class MilestoneItemPullRequest(

    /**
     * Identifies the pull request title.
     */
    val title: String,

    /**
     * Identifies the pull request number.
     */
    val number: Int,

    val id: String,

    /**
     * The HTTP URL for this pull request
     */
    val url: Uri

)

fun MilestoneItemPullRequestFragment.toNonNullMilestoneItemPullRequest(): MilestoneItemPullRequest {
    return MilestoneItemPullRequest(title, number, id, url)
}

/**
 * A label for categorizing Issues or Milestones with a given Repository.
 */
data class Label(

    /**
     * Identifies the label color.
     */
    val color: String,

    val id: String,

    /**
     * Identifies the label name.
     */
    val name: String,

    /**
     * The HTTP URL for this label.
     */
    val url: Uri

)

fun IssuePrLabelFragment.toNonNullLabel(): Label {
    return Label(color, id, name, url)
}

data class Assignee(

    /**
     * A special type of user which takes actions on behalf of GitHub Apps.
     */
    val bot: Bot?,

    /**
     * A placeholder user for attribution of imported data on GitHub.
     */
    val mannequin: Mannequin?,

    /**
     * An account on GitHub, with one or more owners, that has repositories, members and teams.
     */
    val organization: Organization?,

    /**
     * A user is an individual's account on GitHub that owns repositories and can make new content.
     */
    val user: User?

) {

    val assigneeLogin = user?.login ?: organization?.login ?: mannequin?.login ?: bot?.login

}

fun IssuePullRequestTimelineItemAssigneeFragment?.toNonNullAssignee(): Assignee {
    val assignee = this?.fragments
    return Assignee(
        assignee?.issuePullRequestTimelineItemBotFragment?.toNonNullBot(),
        assignee?.issuePullRequestTimelineItemMannequinFragment?.toNonNullMannequin(),
        assignee?.issuePullRequestTimelineItemOrganizationFragment?.toNonNullOrganization(),
        assignee?.issuePullRequestTimelineItemUserFragment?.toNonNullUser()
    )
}

data class PullRequestTimelineItemDeployment(

    /**
     * Identifies the commit sha of the deployment.
     */
    val commit: PullRequestTimelineItemCommit?,

    /**
     * Identifies the oid of the deployment commit, even if the commit has been deleted.
     */
    val commitOid: String,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    /**
     * Identifies the actor who triggered the deployment.
     */
    val creator: Actor?,

    /**
     * The deployment description.
     */
    val description: String?,

    /**
     * The environment to which this deployment was made.
     */
    val environment: String?,

    val id: String,

    /**
     * Identifies the Ref of the deployment, if the deployment was created by ref.
     */
    val ref: PullRequestTimelineItemRef?,

    /**
     * The current state of the deployment.
     */
    val state: DeploymentState?,

    /**
     * The deployment task.
     */
    val task: String?,

    /**
     * Identifies the date and time when the object was last updated.
     */
    val updatedAt: Instant

)

fun PullRequestTimelineItemDeploymentFragment.toNonNullPullRequestTimelineItemDeployment(): PullRequestTimelineItemDeployment {
    return PullRequestTimelineItemDeployment(
        commit?.fragments?.pullRequestTimelineItemCommitFragment?.toNonNullPullRequestTimelineItemCommit(),
        commitOid,
        createdAt,
        creator.fragments.actor.toNonNullActor(),
        description,
        description,
        id,
        ref?.fragments?.pullRequestTimelineItemRefFragment?.toNonNullPullRequestTimelineItemRef(),
        state,
        task,
        updatedAt
    )
}

/**
 * Represents a Git commit.
 */
data class PullRequestTimelineItemCommit(

    /**
     * Authorship details of the commit.
     */
    val author: PullRequestTimelineItemGitActor?,

    /**
     * Committership details of the commit.
     */
    val committer: PullRequestTimelineItemGitActor?,

    /**
     * The Git commit message
     */
    val message: String,

    /**
     * The Git object ID
     */
    val oid: String,

    /**
     * The HTTP URL for this commit
     */
    val url: Uri

)

fun PullRequestTimelineItemCommitFragment.toNonNullPullRequestTimelineItemCommit(): PullRequestTimelineItemCommit {
    return PullRequestTimelineItemCommit(
        author?.fragments?.gitActorFragment?.toNonNullPullRequestTimelineItemGitActor(),
        committer?.fragments?.gitActorFragment?.toNonNullPullRequestTimelineItemGitActor(),
        message,
        oid,
        url
    )
}

data class PullRequestTimelineItemPullRequest(

    /**
     * `true` if the pull request is closed
     */
    val closed: Boolean,

    /**
     * Identifies the pull request number.
     */
    val number: Int,

    val id: String,

    /**
     * Identifies the state of the pull request.
     */
    val state: PullRequestState,

    /**
     * Identifies the pull request title.
     */
    val title: String,

    /**
     * The HTTP URL for this pull request.
     */
    val url: Uri

)

fun RawPullRequestTimelineItemPullRequest.toNonNullPullRequestTimelineItemPullRequest(): PullRequestTimelineItemPullRequest {
    return PullRequestTimelineItemPullRequest(closed, number, id, state, title, url)
}

/**
 *
 */
data class PullRequestTimelineItemRef(

    val id: String,

    /**
     * The ref name.
     */
    val name: String,

    /**
     * The ref's prefix, such as refs/heads/ or refs/tags/.
     */
    val prefix: String

)

fun PullRequestTimelineItemRefFragment.toNonNullPullRequestTimelineItemRef(): PullRequestTimelineItemRef {
    return PullRequestTimelineItemRef(id, name, prefix)
}

data class PullRequestTimelineItemPullRequestReview(

    val id: String,

    /**
     * Identifies the actor who performed the event.
     */
    val author: Actor?,

    /**
     * The HTTP URL permalink for this PullRequestReview.
     */
    val url: Uri

)

fun PullRequestReviewFragment.toNonNullPullRequestTimelineItemPullRequestReview(): PullRequestTimelineItemPullRequestReview {
    return PullRequestTimelineItemPullRequestReview(
        id,
        author?.fragments?.actor?.toNonNullActor(),
        url
    )
}

data class PullRequestTimelineItemGitActor(

    /**
     * A URL pointing to the author's public avatar.
     */
    val avatarUrl: Uri,

    /**
     * The email in the Git commit.
     */
    val email: String?,

    /**
     * The name in the Git commit.
     */
    val name: String?,

    /**
     * The GitHub user corresponding to the email field. Null if no such user exists.
     */
    val user: User?

)

fun GitActorFragment.toNonNullPullRequestTimelineItemGitActor(): PullRequestTimelineItemGitActor {
    return PullRequestTimelineItemGitActor(
        avatarUrl,
        email,
        name,
        user?.fragments?.issuePullRequestTimelineItemUserFragment?.toNonNullUser()
    )
}

// ===== base issue/pull request common definition =====

/**
 * Represents a 'added_to_project' event on a given issue or pull request.
 */
data class AddedToProjectEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String

) : IssueTimelineItem, PullRequestTimelineItem

fun AddedToProjectEventFragment.toNonNullAddedToProjectEvent(): AddedToProjectEvent {
    return AddedToProjectEvent(actor?.fragments?.actor?.toNonNullActor(), createdAt, id)
}

/**
 * Represents an 'assigned' event on any assignable object.
 */
data class AssignedEvent(
    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * The username used to login.
     */
    val assigneeLogin: String?,

    /**
     * The user's public profile name.
     */
    val assigneeName: String?

) : IssueTimelineItem, PullRequestTimelineItem

fun AssignedEventFragment.toNonNullAssignedEvent(): AssignedEvent {
    val assignee = assignee
        ?.fragments
        ?.issuePullRequestTimelineItemAssigneeFragment
        ?.fragments

    var assigneeLogin: String? = null
    var assigneeName: String? = null

    assignee?.issuePullRequestTimelineItemBotFragment?.let {
        assigneeLogin = it.login
        assigneeName = null
    } ?: assignee?.issuePullRequestTimelineItemMannequinFragment?.let {
        assigneeLogin = it.login
        assigneeName = null
    } ?: assignee?.issuePullRequestTimelineItemOrganizationFragment?.let {
        assigneeLogin = it.login
        assigneeName = it.name
    } ?: assignee?.issuePullRequestTimelineItemUserFragment?.let {
        assigneeLogin = it.login
        assigneeName = it.name
    }

    return AssignedEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        id,
        assigneeLogin,
        assigneeName
    )
}

/**
 * Represents a 'closed' event on any Closable.
 */
data class ClosedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,
    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * The HTTP URL for this closed event.
     */
    val url: Uri

) : IssueTimelineItem, PullRequestTimelineItem

fun ClosedEventFragment.toNonNullClosedEvent(): ClosedEvent {
    return ClosedEvent(actor?.fragments?.actor?.toNonNullActor(), createdAt, id, url)
}

/**
 * Represents a 'converted_note_to_issue' event on a given issue or pull request.
 */
data class ConvertedNoteToIssueEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String

) : IssueTimelineItem, PullRequestTimelineItem

fun ConvertedNoteToIssueEventFragment.toNonNullConvertedNoteToIssueEvent(): ConvertedNoteToIssueEvent {
    return ConvertedNoteToIssueEvent(actor?.fragments?.actor?.toNonNullActor(), createdAt, id)
}

/**
 * Represents a mention made by one issue or pull request to another.
 */
data class CrossReferencedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    /**
     * Reference originated in a different repository.
     */
    val isCrossRepository: Boolean,

    override val id: String,

    /**
     * Issue that made the reference.
     */
    val issue: ReferencedEventIssueFragmentItem?,

    /**
     * Pull request that made the reference.
     */
    val pullRequest: ReferencedEventPullRequestFragmentItem?

) : IssueTimelineItem, PullRequestTimelineItem

fun CrossReferencedEventFragment.toNonNullCrossReferencedEvent(): CrossReferencedEvent {
    return CrossReferencedEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        isCrossRepository,
        id,
        source.fragments.referencedEventIssueFragment?.toNonNullReferencedEventIssueFragmentItem(),
        source.fragments.referencedEventPullRequestFragment?.toNonNullReferencedEventPullRequestFragmentItem()
    )
}

/**
 * Represents a 'demilestoned' event on a given issue or pull request.
 */
data class DemilestonedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * Identifies the milestone title associated with the 'demilestoned' event.
     */
    val milestoneTitle: String,

    val subjectIssue: MilestoneItemIssue?,

    val subjectPullRequest: MilestoneItemPullRequest?

) : IssueTimelineItem, PullRequestTimelineItem

fun DemilestonedEventFragment.toNonNullDemilestonedEvent(): DemilestonedEvent {
    return DemilestonedEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        id,
        milestoneTitle,
        subject.fragments.milestoneItemIssueFragment?.toNonNullMilestoneItemIssue(),
        subject.fragments.milestoneItemPullRequestFragment?.toNonNullMilestoneItemPullRequest()
    )
}

/**
 * Represents a comment on an Issue.
 */
data class IssueComment(

    /**
     * The actor who authored the comment.
     */
    val author: Actor?,

    val authorAssociation: CommentAuthorAssociation,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    val displayHtml: String,

    override val id: String,

    /**
     * The actor who edited the comment.
     */
    val editor: Actor?,

    /**
     * A list of reactions grouped by content left on the subject.
     */
    var reactionGroups: MutableList<ReactionGroup>?,

    /**
     * Check if the current viewer can delete this object.
     */
    val viewerCanDelete: Boolean,

    /**
     * Can user react to this subject
     */
    val viewerCanReact: Boolean,

    /**
     * Did the viewer author this comment.
     */
    val viewerDidAuthor: Boolean,

    /**
     * Check if the current viewer can update this object.
     */
    val viewerCanUpdate: Boolean,

    /**
     * Check if the current viewer can minimize this object.
     */
    val viewerCanMinimize: Boolean,

    /**
     * Reasons why the current viewer can not update this comment.
     */
    val viewerCannotUpdateReasons: List<CommentCannotUpdateReason>

) : IssueTimelineItem, PullRequestTimelineItem

@WorkerThread
fun IssueCommentFragment.toNonNullIssueComment(
    login: String,
    repoName: String
): IssueComment {
    return IssueComment(
        author?.fragments?.actor?.toNonNullActor(),
        authorAssociation,
        createdAt,
        HtmlHandler.toHtml(body, login, repoName),
        id,
        editor?.fragments?.actor?.toNonNullActor(),
        reactionGroups?.filter {
            it.fragments.reactionGroup.users.totalCount > 0
        }?.map {
            it.fragments.reactionGroup.toNonNullReactionGroup()
        }?.toMutableList(),
        viewerCanDelete,
        viewerCanReact,
        viewerDidAuthor,
        viewerCanUpdate,
        viewerCanMinimize,
        viewerCannotUpdateReasons
    )
}

/**
 * Represents a 'labeled' event on a given issue or pull request.
 */
data class LabeledEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * Identifies the label associated with the 'labeled' event.
     */
    val label: Label

) : IssueTimelineItem, PullRequestTimelineItem

fun LabeledEventFragment.toNonNullLabeledEvent(): LabeledEvent {
    return LabeledEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        id,
        label.fragments.issuePrLabelFragment.toNonNullLabel()
    )
}

/**
 * Represents a 'locked' event on a given issue or pull request.
 */
data class LockedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * Reason that the conversation was locked (optional).
     */
    val lockReason: LockReason?

) : IssueTimelineItem, PullRequestTimelineItem

fun LockedEventFragment.toNonNullLockedEvent(): LockedEvent {
    return LockedEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        id,
        lockReason
    )
}

/**
 * Represents a 'marked_as_duplicate' event on a given issue or pull request.
 */
data class MarkedAsDuplicateEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String

) : IssueTimelineItem, PullRequestTimelineItem

fun MarkedAsDuplicateEventFragment.toNonNullMarkedAsDuplicateEvent(): MarkedAsDuplicateEvent {
    return MarkedAsDuplicateEvent(actor?.fragments?.actor?.toNonNullActor(), createdAt, id)
}

/**
 * Represents a 'milestoned' event on a given issue or pull request.
 */
data class MilestonedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * Identifies the milestone title associated with the 'milestoned' event.
     */
    val milestoneTitle: String

) : IssueTimelineItem, PullRequestTimelineItem

fun MilestonedEventFragment.toNonNullMilestonedEvent(): MilestonedEvent {
    return MilestonedEvent(actor?.fragments?.actor?.toNonNullActor(), createdAt, id, milestoneTitle)
}

/**
 * Represents a 'moved_columns_in_project' event on a given issue or pull request.
 */
data class MovedColumnsInProjectEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String

) : IssueTimelineItem, PullRequestTimelineItem

fun MovedColumnsInProjectEventFragment.toNonNullMovedColumnsInProjectEvent(): MovedColumnsInProjectEvent {
    return MovedColumnsInProjectEvent(actor?.fragments?.actor?.toNonNullActor(), createdAt, id)
}

/**
 * Represents a 'pinned' event on a given issue or pull request.
 */
data class PinnedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String

) : IssueTimelineItem, PullRequestTimelineItem

fun PinnedEventFragment.toNonNullPinnedEvent(): PinnedEvent {
    return PinnedEvent(actor?.fragments?.actor?.toNonNullActor(), createdAt, id)
}

/**
 * Represents a 'referenced' event on a given ReferencedSubject.
 */
data class ReferencedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * Reference originated in a different repository.
     */
    val isCrossRepository: Boolean,

    /**
     * Checks if the commit message itself references the subject.
     * Can be false in the case of a commit comment reference.
     */
    val isDirectReference: Boolean,

    val issue: ReferencedEventIssueFragmentItem?,

    val pullRequest: ReferencedEventPullRequestFragmentItem?

) : IssueTimelineItem, PullRequestTimelineItem

fun ReferencedEventFragment.toNonNullReferencedEvent(): ReferencedEvent {
    return ReferencedEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        id,
        isCrossRepository,
        isDirectReference,
        subject.fragments.referencedEventIssueFragment?.toNonNullReferencedEventIssueFragmentItem(),
        subject.fragments.referencedEventPullRequestFragment?.toNonNullReferencedEventPullRequestFragmentItem()
    )
}

/**
 * Represents a 'removed_from_project' event on a given issue or pull request.
 */
data class RemovedFromProjectEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String

) : IssueTimelineItem, PullRequestTimelineItem

fun RemovedFromProjectEventFragment.toNonNullRemovedFromProjectEvent(): RemovedFromProjectEvent {
    return RemovedFromProjectEvent(actor?.fragments?.actor?.toNonNullActor(), createdAt, id)
}

/**
 * Represents a 'renamed' event on a given issue or pull request
 */
data class RenamedTitleEvent(

    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    /**
     * Identifies the current title of the issue or pull request.
     */
    val currentTitle: String,

    override val id: String,

    /**
     * Identifies the previous title of the issue or pull request.
     */
    val previousTitle: String

) : IssueTimelineItem, PullRequestTimelineItem

fun RenamedTitleEventFragment.toNonNullRenamedTitleEvent(): RenamedTitleEvent {
    return RenamedTitleEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        currentTitle,
        id,
        previousTitle
    )
}

/**
 * Represents a 'reopened' event on any Closable.
 */
data class ReopenedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String

) : IssueTimelineItem, PullRequestTimelineItem

fun ReopenedEventFragment.toNonNullReopenedEvent(): ReopenedEvent {
    return ReopenedEvent(actor?.fragments?.actor?.toNonNullActor(), createdAt, id)
}

/**
 * Represents a 'subscribed' event on a given `Subscribable`.
 */
data class TransferredEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * The repository's name with owner.
     */
    val nameWithOwnerOfFromRepository: String?,

    val ownerOrganization: Organization?,

    val ownerUser: User?

) : IssueTimelineItem, PullRequestTimelineItem

fun TransferredEventFragment.toNonNullTransferredEvent(): TransferredEvent {
    val owner = fromRepository?.owner?.fragments
    return TransferredEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        id,
        fromRepository?.nameWithOwner,
        owner?.issuePullRequestTimelineItemOrganizationFragment?.toNonNullOrganization(),
        owner?.issuePullRequestTimelineItemUserFragment?.toNonNullUser()
    )
}

/**
 * Represents an 'unassigned' event on any assignable object.
 */
data class UnassignedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    val assignee: Assignee

) : IssueTimelineItem, PullRequestTimelineItem

fun UnassignedEventFragment.toNonNullUnassignedEvent(): UnassignedEvent {
    return UnassignedEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        id,
        assignee?.fragments?.issuePullRequestTimelineItemAssigneeFragment.toNonNullAssignee()
    )
}

/**
 * Represents an 'unlabeled' event on a given issue or pull request.
 */
data class UnlabeledEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * Identifies the label associated with the 'unlabeled' event.
     */
    val label: Label

) : IssueTimelineItem, PullRequestTimelineItem

fun UnlabeledEventFragment.toNonNullUnlabeledEvent(): UnlabeledEvent {
    return UnlabeledEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        id,
        label.fragments.issuePrLabelFragment.toNonNullLabel()
    )
}

/**
 * Represents an 'unlocked' event on a given issue or pull request.
 */
data class UnlockedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    /**
     * Identifies the date and time when the object was created.
     */
    override val id: String

) : IssueTimelineItem, PullRequestTimelineItem

fun UnlockedEventFragment.toNonNullUnlockedEvent(): UnlockedEvent {
    return UnlockedEvent(actor?.fragments?.actor?.toNonNullActor(), createdAt, id)
}

/**
 * Represents an 'unpinned' event on a given issue or pull request.
 */
data class UnpinnedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String

) : IssueTimelineItem, PullRequestTimelineItem

fun UnpinnedEventFragment.toNonNullUnpinnedEvent(): UnpinnedEvent {
    return UnpinnedEvent(actor?.fragments?.actor?.toNonNullActor(), createdAt, id)
}

data class ReferencedEventIssueFragmentItem(

    val title: String,

    val number: Int,

    val id: String

)

fun ReferencedEventIssueFragment.toNonNullReferencedEventIssueFragmentItem(): ReferencedEventIssueFragmentItem {
    return ReferencedEventIssueFragmentItem(title, number, id)
}

data class ReferencedEventPullRequestFragmentItem(

    val title: String,

    val number: Int,

    val id: String

)

fun ReferencedEventPullRequestFragment.toNonNullReferencedEventPullRequestFragmentItem(): ReferencedEventPullRequestFragmentItem {
    return ReferencedEventPullRequestFragmentItem(title, number, id)
}

data class BaseRefChangedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String

) : PullRequestTimelineItem

fun BaseRefChangedEventFragment.toNonNullBaseRefChangedEvent(): BaseRefChangedEvent {
    return BaseRefChangedEvent(actor?.fragments?.actor?.toNonNullActor(), createdAt, id)
}

data class BaseRefForcePushedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the after commit SHA for the 'base_ref_force_pushed' event.
     */
    val afterCommit: PullRequestTimelineItemCommit?,

    /**
     * Identifies the before commit SHA for the 'base_ref_force_pushed' event.
     */
    val beforeCommit: PullRequestTimelineItemCommit?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * Identifies the fully qualified ref name for the 'base_ref_force_pushed' event.
     */
    val ref: PullRequestTimelineItemRef?

) : PullRequestTimelineItem

fun BaseRefForcePushedEventFragment.toNonNullBaseRefForcePushedEvent(): BaseRefForcePushedEvent {
    return BaseRefForcePushedEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        afterCommit?.fragments?.pullRequestTimelineItemCommitFragment?.toNonNullPullRequestTimelineItemCommit(),
        beforeCommit?.fragments?.pullRequestTimelineItemCommitFragment?.toNonNullPullRequestTimelineItemCommit(),
        createdAt,
        id,
        ref?.fragments?.pullRequestTimelineItemRefFragment?.toNonNullPullRequestTimelineItemRef()
    )
}

data class PullRequestCommit(

    /**
     * The Git commit object
     */
    val commit: PullRequestTimelineItemCommit,

    override val id: String,

    /**
     * The pull request this commit belongs to
     */
    val pullRequest: PullRequestTimelineItemPullRequest,

    /**
     * The HTTP URL for this pull request commit
     */
    val url: Uri

) : PullRequestTimelineItem

fun PullRequestCommitFragment.toNonNullPullRequestCommit(): PullRequestCommit {
    return PullRequestCommit(
        commit.fragments.pullRequestTimelineItemCommitFragment.toNonNullPullRequestTimelineItemCommit(),
        id,
        pullRequest.fragments.pullRequestTimelineItemPullRequest.toNonNullPullRequestTimelineItemPullRequest(),
        url
    )
}

data class PullRequestCommitCommentThread(

    /**
     * The commit the comments were made on.
     */
    val commit: PullRequestTimelineItemCommit,

    override val id: String,

    /**
     * The file the comments were made on.
     */
    val path: String?,

    /**
     * The position in the diff for the commit that the comment was made on.
     */
    val position: Int?,

    /**
     * The pull request this commit comment thread belongs to.
     */
    val pullRequest: PullRequestTimelineItemPullRequest

) : PullRequestTimelineItem

fun PullRequestCommitCommentThreadFragment.toNonNullPullRequestCommitCommentThread(): PullRequestCommitCommentThread {
    return PullRequestCommitCommentThread(
        commit.fragments.pullRequestTimelineItemCommitFragment.toNonNullPullRequestTimelineItemCommit(),
        id,
        path,
        position,
        pullRequest.fragments.pullRequestTimelineItemPullRequest.toNonNullPullRequestTimelineItemPullRequest()
    )
}

data class DeployedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    val deploymentEnvironment: String?

) : PullRequestTimelineItem

fun DeployedEventFragment.toNonNullDeployedEvent(): DeployedEvent {
    return DeployedEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        id,
        deployment.fragments.pullRequestTimelineItemDeploymentFragment.environment
    )
}

/**
 * Represents a 'deployment_environment_changed' event on a given pull request.
 */
data class DeploymentEnvironmentChangedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    /**
     * Identifies the deployment associated with status.
     */
    val deployment: PullRequestTimelineItemDeployment,

    override val id: String

) : PullRequestTimelineItem

fun DeploymentEnvironmentChangedEventFragment.toNonNullDeploymentEnvironmentChangedEvent(): DeploymentEnvironmentChangedEvent {
    return DeploymentEnvironmentChangedEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        deploymentStatus
            .deployment
            .fragments
            .pullRequestTimelineItemDeploymentFragment
            .toNonNullPullRequestTimelineItemDeployment(),
        id
    )
}

/**
 * Represents a 'head_ref_deleted' event on a given pull request.
 */
data class HeadRefDeletedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * Identifies the name of the Ref associated with the head_ref_deleted event.
     */
    val headRefName: String

) : PullRequestTimelineItem

fun HeadRefDeletedEventFragment.toNonNullHeadRefDeletedEvent(): HeadRefDeletedEvent {
    return HeadRefDeletedEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        id,
        headRefName
    )
}

/**
 * Represents a 'head_ref_force_pushed' event on a given pull request.
 */
data class HeadRefForcePushedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the after commit SHA for the 'head_ref_force_pushed' event.
     */
    val afterCommitOid: String?,

    /**
     * Identifies the before commit SHA for the 'head_ref_force_pushed' event.
     */
    val beforeCommitOid: String?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * PullRequest referenced by event.
     */
    val pullRequest: PullRequestTimelineItemPullRequest,

    /**
     * Identifies the fully qualified ref name for the 'head_ref_force_pushed' event.
     */
    val ref: PullRequestTimelineItemRef?

) : PullRequestTimelineItem

fun HeadRefForcePushedEventFragment.toNonNullHeadRefForcePushedEvent(): HeadRefForcePushedEvent {
    return HeadRefForcePushedEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        afterCommit?.fragments?.pullRequestTimelineItemCommitFragment?.oid,
        beforeCommit?.fragments?.pullRequestTimelineItemCommitFragment?.oid,
        createdAt,
        id,
        pullRequest.fragments.pullRequestTimelineItemPullRequest.toNonNullPullRequestTimelineItemPullRequest(),
        ref?.fragments?.pullRequestTimelineItemRefFragment?.toNonNullPullRequestTimelineItemRef()
    )
}

/**
 * Represents a 'head_ref_restored' event on a given pull request.
 */
data class HeadRefRestoredEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * Identifies the name of the base Ref associated with the pull request, even if the ref has been deleted.
     */
    val pullRequestBaseRefName: String,

    /**
     * Identifies the name of the head Ref associated with the pull request, even if the ref has been deleted.
     */
    val pullRequestHeadRefName: String,

    val pullRequest: PullRequestTimelineItemPullRequest

) : PullRequestTimelineItem

fun HeadRefRestoredEventFragment.toNonNullHeadRefRestoredEvent(): HeadRefRestoredEvent {
    return HeadRefRestoredEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        id,
        pullRequest.baseRefName,
        pullRequest.headRefName,
        pullRequest.fragments.pullRequestTimelineItemPullRequest.toNonNullPullRequestTimelineItemPullRequest()
    )
}

data class MergedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,
    /**
     * Identifies the name of the Ref associated with the merge event.
     */
    val mergeRefName: String,

    /**
     * ASCII-armored signature header from object.
     */
    val commitOid: String?

) : PullRequestTimelineItem

fun MergedEventFragment.toNonNullMergedEvent(): MergedEvent {
    return MergedEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        id,
        mergeRefName,
        commit?.fragments?.pullRequestTimelineItemCommitFragment?.oid
    )
}

data class PullRequestReview(

    /**
     * Identifies the actor who performed the event.
     */
    val author: Actor?,

    /**
     * Author's association with the subject of the comment.
     */
    val authorAssociation: CommentAuthorAssociation,

    /**
     * Identifies the pull request review body.
     */
    val body: String,

    /**
     * The body of this review rendered to HTML.
     */
    val bodyHTML: String,

    /**
     * Identifies the commit associated with this pull request review.
     */
    val commit: PullRequestTimelineItemCommit?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    /**
     * Check if this comment was created via an email reply.
     */
    val createdViaEmail: Boolean,

    /**
     * The actor who edited the comment.
     */
    val editor: Actor?,

    override val id: String,

    /**
     * Check if this comment was edited and includes an edit with the creation data
     */
    val includesCreatedEdit: Boolean,

    /**
     * The moment the editor made the last edit
     */
    val lastEditedAt: Instant?,

    /**
     * Identifies when the comment was published at.
     */
    val publishedAt: Instant?,

    /**
     * Identifies the pull request associated with this pull request review.
     */
    val pullRequest: PullRequestTimelineItemPullRequest,

    /**
     * Identifies the current state of the pull request review.
     */
    val state: PullRequestReviewState,

    /**
     * Identifies when the Pull Request Review was submitted
     */
    val submittedAt: Instant?,

    /**
     * Identifies the date and time when the object was last updated.
     */
    val updatedAt: Instant,

    /**
     * The HTTP URL permalink for this PullRequestReview.
     */
    val url: Uri,

    /**
     * Check if the current viewer can delete this object.
     */
    val viewerCanDelete: Boolean,

    /**
     * Can user react to this subject
     */
    val viewerCanReact: Boolean,

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

    val commentCount: Int

) : PullRequestTimelineItem

fun PullRequestReviewFragment.toNonNullPullRequestReview(): PullRequestReview {
    return PullRequestReview(
        author?.fragments?.actor?.toNonNullActor(),
        authorAssociation,
        body,
        bodyHTML,
        commit?.fragments?.pullRequestTimelineItemCommitFragment?.toNonNullPullRequestTimelineItemCommit(),
        createdAt,
        createdViaEmail,
        editor?.fragments?.actor?.toNonNullActor(),
        id,
        includesCreatedEdit,
        lastEditedAt,
        publishedAt,
        pullRequest.fragments.pullRequestTimelineItemPullRequest.toNonNullPullRequestTimelineItemPullRequest(),
        state,
        submittedAt,
        updatedAt,
        url,
        viewerCanDelete,
        viewerCanReact,
        viewerCanUpdate,
        viewerCannotUpdateReasons,
        viewerDidAuthor,
        comments.totalCount
    )
}

data class PullRequestReviewThread(

    override val id: String,

    /**
     * Whether this thread has been resolved
     */
    val isResolved: Boolean,

    /**
     * The user who resolved this thread
     */
    val resolvedBy: User?,

    /**
     * Identifies the pull request associated with this thread.
     */
    val pullRequest: PullRequestTimelineItemPullRequest,

    /**
     * Whether or not the viewer can resolve this thread
     */
    val viewerCanResolve: Boolean,

    /**
     * Whether or not the viewer can unresolve this thread
     */
    val viewerCanUnresolve: Boolean

) : PullRequestTimelineItem

fun PullRequestReviewThreadFragment.toNonNullPullRequestReviewThread(): PullRequestReviewThread {
    return PullRequestReviewThread(
        id,
        isResolved,
        resolvedBy?.fragments?.issuePullRequestTimelineItemUserFragment?.toNonNullUser(),
        pullRequest.fragments.pullRequestTimelineItemPullRequest.toNonNullPullRequestTimelineItemPullRequest(),
        viewerCanResolve,
        viewerCanUnresolve
    )
}

/**
 * Represents a 'ready_for_review' event on a given pull request.
 */
data class ReadyForReviewEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * PullRequest referenced by event.
     */
    val pullRequest: PullRequestTimelineItemPullRequest,

    /**
     * The HTTP URL for this ready for review event.
     */
    val url: Uri

) : PullRequestTimelineItem

fun ReadyForReviewEventFragment.toNonNullReadyForReviewEvent(): ReadyForReviewEvent {
    return ReadyForReviewEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        id,
        pullRequest.fragments.pullRequestTimelineItemPullRequest.toNonNullPullRequestTimelineItemPullRequest(),
        url
    )
}

/**
 * Represents a 'review_dismissed' event on a given issue or pull request.
 */
data class ReviewDismissedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    /**
     * Identifies the message associated with the 'review_dismissed' event.
     */
    val dismissalMessage: String?,
    /**
     * Identifies the optional message associated with the event, rendered to HTML.
     */
    val dismissalMessageHTML: String?,

    override val id: String,

    /**
     * Identifies the previous state of the review with the 'review_dismissed' event.
     */
    val previousReviewState: PullRequestReviewState,

    val review: PullRequestTimelineItemPullRequestReview?,

    val url: Uri

) : PullRequestTimelineItem

fun ReviewDismissedEventFragment.toNonNullReviewDismissedEvent(): ReviewDismissedEvent {
    return ReviewDismissedEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        dismissalMessage,
        dismissalMessageHTML,
        id,
        previousReviewState,
        review?.fragments?.pullRequestReviewFragment?.toNonNullPullRequestTimelineItemPullRequestReview(),
        url
    )
}

/**
 * Represents an 'review_request_removed' event on a given pull request.
 */
data class ReviewRequestRemovedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * PullRequest referenced by event.
     */
    val pullRequest: PullRequestTimelineItemPullRequest,

    val requestedReviewerTeam: Team?,

    val requestedReviewerUser: User?,

    val requestedReviewerMannequin: Mannequin?

) : PullRequestTimelineItem

fun ReviewRequestRemovedEventFragment.toNonNullReviewRequestRemovedEvent(): ReviewRequestRemovedEvent {
    return ReviewRequestRemovedEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        id,
        pullRequest.fragments.pullRequestTimelineItemPullRequest.toNonNullPullRequestTimelineItemPullRequest(),
        requestedReviewer?.fragments?.issuePullRequestTimelineItemTeamFragment?.toNonNullTeam(),
        requestedReviewer?.fragments?.issuePullRequestTimelineItemUserFragment?.toNonNullUser(),
        requestedReviewer?.fragments?.issuePullRequestTimelineItemMannequinFragment?.toNonNullMannequin()
    )
}

/**
 * Represents an 'review_requested' event on a given pull request.
 */
data class ReviewRequestedEvent(

    /**
     * Identifies the actor who performed the event.
     */
    val actor: Actor?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    override val id: String,

    /**
     * PullRequest referenced by event.
     */
    val pullRequest: PullRequestTimelineItemPullRequest,

    val requestedReviewerTeam: Team?,

    val requestedReviewerUser: User?,

    val requestedReviewerMannequin: Mannequin?

) : PullRequestTimelineItem {

    @IgnoredOnParcel
    val requestedReviewerLogin = requestedReviewerUser?.login
        ?: requestedReviewerTeam?.combinedSlug
        ?: requestedReviewerMannequin?.login

}

fun ReviewRequestedEventFragment.toNonNullReviewRequestedEvent(): ReviewRequestedEvent {
    return ReviewRequestedEvent(
        actor?.fragments?.actor?.toNonNullActor(),
        createdAt,
        id,
        pullRequest.fragments.pullRequestTimelineItemPullRequest.toNonNullPullRequestTimelineItemPullRequest(),
        requestedReviewer?.fragments?.issuePullRequestTimelineItemTeamFragment?.toNonNullTeam(),
        requestedReviewer?.fragments?.issuePullRequestTimelineItemUserFragment?.toNonNullUser(),
        requestedReviewer?.fragments?.issuePullRequestTimelineItemMannequinFragment?.toNonNullMannequin()
    )
}