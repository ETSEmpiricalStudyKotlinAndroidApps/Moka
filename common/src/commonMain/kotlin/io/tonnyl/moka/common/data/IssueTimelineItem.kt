package io.tonnyl.moka.common.data

import io.tonnyl.moka.graphql.fragment.*

data class IssueTimelineItem(

    val addedToProjectEvent: AddedToProjectEventFragment? = null,

    val assignedEvent: AssignedEventFragment? = null,

    val closedEvent: ClosedEventFragment? = null,

    val convertedNoteToIssueEvent: ConvertedNoteToIssueEventFragment? = null,

    val crossReferencedEvent: CrossReferencedEventFragment? = null,

    val demilestonedEvent: DemilestonedEventFragment? = null,

    val issueComment: IssueCommentFragment? = null,

    val labeledEvent: LabeledEventFragment? = null,

    val lockedEvent: LockedEventFragment? = null,

    val markedAsDuplicateEvent: MarkedAsDuplicateEventFragment? = null,

    val milestonedEvent: MilestonedEventFragment? = null,

    val movedColumnsInProjectEvent: MovedColumnsInProjectEventFragment? = null,

    val pinnedEvent: PinnedEventFragment? = null,

    val referencedEvent: ReferencedEventFragment? = null,

    val removedFromProjectEvent: RemovedFromProjectEventFragment? = null,

    val renamedTitleEvent: RenamedTitleEventFragment? = null,

    val reopenedEvent: ReopenedEventFragment? = null,

    val transferredEvent: TransferredEventFragment? = null,

    val unassignedEvent: UnassignedEventFragment? = null,

    val unlabeledEvent: UnlabeledEventFragment? = null,

    val unlockedEvent: UnlockedEventFragment? = null,

    val unpinnedEvent: UnpinnedEventFragment? = null

)