package io.tonnyl.moka.common.data

import io.tonnyl.moka.graphql.fragment.*

data class PullRequestTimelineItem(

    val addedToProjectEvent: AddedToProjectEventFragment? = null,

    val assignedEvent: AssignedEventFragment? = null,

    val baseRefChangedEvent: BaseRefChangedEventFragment? = null,

    val baseRefForcePushedEvent: BaseRefForcePushedEventFragment? = null,

    val closedEvent: ClosedEventFragment? = null,

    val convertedNoteToIssueEvent: ConvertedNoteToIssueEventFragment? = null,

    val crossReferencedEvent: CrossReferencedEventFragment? = null,

    val demilestonedEvent: DemilestonedEventFragment? = null,

    val deployedEvent: DeployedEventFragment? = null,

    val deploymentEnvironmentChangedEvent: DeploymentEnvironmentChangedEventFragment? = null,

    val headRefDeletedEvent: HeadRefDeletedEventFragment? = null,

    val headRefForcePushedEvent: HeadRefForcePushedEventFragment? = null,

    val headRefRestoredEvent: HeadRefRestoredEventFragment? = null,

    val issueComment: IssueCommentFragment? = null,

    val labeledEvent: LabeledEventFragment? = null,

    val lockedEvent: LockedEventFragment? = null,

    val markedAsDuplicateEvent: MarkedAsDuplicateEventFragment? = null,

    val mergedEvent: MergedEventFragment? = null,

    val milestonedEvent: MilestonedEventFragment? = null,

    val movedColumnsInProjectEvent: MovedColumnsInProjectEventFragment? = null,

    val pinnedEvent: PinnedEventFragment? = null,

    val pullRequestCommit: PullRequestCommitFragment? = null,

    val pullRequestCommitCommentThread: PullRequestCommitCommentThreadFragment? = null,

    val pullRequestReview: PullRequestReviewFragment? = null,

    val pullRequestReviewThread: PullRequestReviewThreadFragment? = null,

    val readyForReviewEvent: ReadyForReviewEventFragment? = null,

    val referencedEvent: ReferencedEventFragment? = null,

    val removedFromProjectEvent: RemovedFromProjectEventFragment? = null,

    val renamedTitleEvent: RenamedTitleEventFragment? = null,

    val reopenedEvent: ReopenedEventFragment? = null,

    val reviewDismissedEvent: ReviewDismissedEventFragment? = null,

    val reviewRequestRemovedEvent: ReviewRequestRemovedEventFragment? = null,

    val reviewRequestedEvent: ReviewRequestedEventFragment? = null,

    val transferredEvent: TransferredEventFragment? = null,

    val unassignedEvent: UnassignedEventFragment? = null,

    val unlabeledEvent: UnlabeledEventFragment? = null,

    val unlockedEvent: UnlockedEventFragment? = null,

    val unpinnedEvent: UnpinnedEventFragment? = null

)