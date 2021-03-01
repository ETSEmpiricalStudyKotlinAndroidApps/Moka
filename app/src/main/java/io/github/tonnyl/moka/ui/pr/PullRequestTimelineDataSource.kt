package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.tonnyl.moka.data.PullRequest
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.data.toNullablePullRequest
import io.github.tonnyl.moka.network.queries.queryPullRequest
import io.github.tonnyl.moka.network.queries.queryPullRequestTimelineItems
import io.github.tonnyl.moka.queries.PullRequestQuery
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PullRequestTimelineDataSource(
    private val owner: String,
    private val name: String,
    private val number: Int,
    private val pullRequestData: MutableLiveData<PullRequest>
) : PagingSource<String, PullRequestTimelineItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PullRequestTimelineItem> {
        val list = mutableListOf<PullRequestTimelineItem>()

        return withContext(Dispatchers.IO) {
            try {
                if (params is LoadParams.Refresh) {
                    val pullRequest = queryPullRequest(
                        owner = owner,
                        name = name,
                        number = number,
                        perPage = params.loadSize,
                        after = params.key,
                        before = params.key
                    ).data()?.repository?.pullRequest

                    pullRequestData.postValue(pullRequest.toNullablePullRequest())

                    val timeline = pullRequest?.timelineItems

                    timeline?.nodes?.forEach { node ->
                        node?.let {
                            initTimelineItemWithRawData(node)?.let {
                                list.add(it)
                            }
                        }
                    }

                    val pageInfo = timeline?.pageInfo?.fragments?.pageInfo

                    LoadResult.Page(
                        data = list,
                        prevKey = pageInfo.checkedStartCursor,
                        nextKey = pageInfo.checkedEndCursor
                    )
                } else {
                    val timeline = queryPullRequestTimelineItems(
                        owner = owner,
                        name = name,
                        number = number,
                        perPage = params.loadSize,
                        after = params.key,
                        before = params.key
                    ).data()?.repository?.pullRequest?.timelineItems

                    timeline?.nodes?.forEach { node ->
                        node?.let {
                            initTimelineItemWithRawData(node)?.let {
                                list.add(it)
                            }
                        }
                    }

                    val pageInfo = timeline?.pageInfo?.fragments?.pageInfo

                    LoadResult.Page(
                        data = list,
                        prevKey = pageInfo.checkedStartCursor,
                        nextKey = pageInfo.checkedEndCursor
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)

                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<String, PullRequestTimelineItem>): String? {
        return null
    }

    private fun initTimelineItemWithRawData(node: PullRequestTimelineItemsQuery.Node): PullRequestTimelineItem? {
        return when {
            node.fragments.addedToProjectEventFragment != null -> {
                node.fragments.addedToProjectEventFragment.toNonNullAddedToProjectEvent()
            }
            node.fragments.assignedEventFragment != null -> {
                node.fragments.assignedEventFragment.toNonNullAssignedEvent()
            }
            node.fragments.baseRefChangedEventFragment != null -> {
                node.fragments.baseRefChangedEventFragment.toNonNullBaseRefChangedEvent()
            }
            node.fragments.baseRefForcePushedEventFragment != null -> {
                node.fragments.baseRefForcePushedEventFragment.toNonNullBaseRefForcePushedEvent()
            }
            node.fragments.closedEventFragment != null -> {
                node.fragments.closedEventFragment.toNonNullClosedEvent()
            }
            // node.fragments.commentDeletedEventFragment != null -> {
            //     node.fragments.commentDeletedEventFragment.toNonNullCommentDeletedEvent()
            // }
            node.fragments.convertedNoteToIssueEventFragment != null -> {
                node.fragments.convertedNoteToIssueEventFragment.toNonNullConvertedNoteToIssueEvent()
            }
            node.fragments.crossReferencedEventFragment != null -> {
                node.fragments.crossReferencedEventFragment.toNonNullCrossReferencedEvent()
            }
            node.fragments.demilestonedEventFragment != null -> {
                node.fragments.demilestonedEventFragment.toNonNullDemilestonedEvent()
            }
            node.fragments.deployedEventFragment != null -> {
                node.fragments.deployedEventFragment.toNonNullDeployedEvent()
            }
            node.fragments.deploymentEnvironmentChangedEventFragment != null -> {
                node.fragments.deploymentEnvironmentChangedEventFragment.toNonNullDeploymentEnvironmentChangedEvent()
            }
            node.fragments.headRefDeletedEventFragment != null -> {
                node.fragments.headRefDeletedEventFragment.toNonNullHeadRefDeletedEvent()
            }
            node.fragments.headRefForcePushedEventFragment != null -> {
                node.fragments.headRefForcePushedEventFragment.toNonNullHeadRefForcePushedEvent()
            }
            node.fragments.headRefRestoredEventFragment != null -> {
                node.fragments.headRefRestoredEventFragment.toNonNullHeadRefRestoredEvent()
            }
            node.fragments.issueCommentFragment != null -> {
                node.fragments.issueCommentFragment.toNonNullIssueComment(owner, name)
            }
            node.fragments.labeledEventFragment != null -> {
                node.fragments.labeledEventFragment.toNonNullLabeledEvent()
            }
            node.fragments.lockedEventFragment != null -> {
                node.fragments.lockedEventFragment.toNonNullLockedEvent()
            }
            node.fragments.markedAsDuplicateEventFragment != null -> {
                node.fragments.markedAsDuplicateEventFragment.toNonNullMarkedAsDuplicateEvent()
            }
            // node.fragments.mentionedEventFragment != null -> {
            //     node.fragments.mentionedEventFragment?.toNonNullMentionedEvent()
            // }
            node.fragments.mergedEventFragment != null -> {
                node.fragments.mergedEventFragment.toNonNullMergedEvent()
            }
            node.fragments.milestonedEventFragment != null -> {
                node.fragments.milestonedEventFragment.toNonNullMilestonedEvent()
            }
            node.fragments.movedColumnsInProjectEventFragment != null -> {
                node.fragments.movedColumnsInProjectEventFragment.toNonNullMovedColumnsInProjectEvent()
            }
            node.fragments.pinnedEventFragment != null -> {
                node.fragments.pinnedEventFragment.toNonNullPinnedEvent()
            }
            node.fragments.pullRequestCommitFragment != null -> {
                node.fragments.pullRequestCommitFragment.toNonNullPullRequestCommit()
            }
            node.fragments.pullRequestCommitCommentThreadFragment != null -> {
                node.fragments.pullRequestCommitCommentThreadFragment.toNonNullPullRequestCommitCommentThread()
            }
            node.fragments.pullRequestReviewFragment != null -> {
                node.fragments.pullRequestReviewFragment.toNonNullPullRequestReview()
            }
            node.fragments.pullRequestReviewThreadFragment != null -> {
                node.fragments.pullRequestReviewThreadFragment.toNonNullPullRequestReviewThread()
            }
            // node.fragments.pullRequestRevisionMarkerFragment != null -> {
            //     node.fragments.pullRequestRevisionMarkerFragment.toNonNullPullRequestRevisionMarker()
            // }
            node.fragments.readyForReviewEventFragment != null -> {
                node.fragments.readyForReviewEventFragment.toNonNullReadyForReviewEvent()
            }
            node.fragments.referencedEventFragment != null -> {
                node.fragments.referencedEventFragment.toNonNullReferencedEvent()
            }
            node.fragments.removedFromProjectEventFragment != null -> {
                node.fragments.removedFromProjectEventFragment.toNonNullRemovedFromProjectEvent()
            }
            node.fragments.renamedTitleEventFragment != null -> {
                node.fragments.renamedTitleEventFragment.toNonNullRenamedTitleEvent()
            }
            node.fragments.reopenedEventFragment != null -> {
                node.fragments.reopenedEventFragment.toNonNullReopenedEvent()
            }
            node.fragments.reviewDismissedEventFragment != null -> {
                node.fragments.reviewDismissedEventFragment.toNonNullReviewDismissedEvent()
            }
            node.fragments.reviewRequestRemovedEventFragment != null -> {
                node.fragments.reviewRequestRemovedEventFragment.toNonNullReviewRequestRemovedEvent()
            }
            node.fragments.reviewRequestedEventFragment != null -> {
                node.fragments.reviewRequestedEventFragment.toNonNullReviewRequestedEvent()
            }
            // node.fragments.subscribedEventFragment != null -> {
            //     node.fragments.subscribedEventFragment.toNonNullSubscribedEvent()
            // }
            node.fragments.transferredEventFragment != null -> {
                node.fragments.transferredEventFragment.toNonNullTransferredEvent()
            }
            node.fragments.unassignedEventFragment != null -> {
                node.fragments.unassignedEventFragment.toNonNullUnassignedEvent()
            }
            node.fragments.unlabeledEventFragment != null -> {
                node.fragments.unlabeledEventFragment.toNonNullUnlabeledEvent()
            }
            node.fragments.unlockedEventFragment != null -> {
                node.fragments.unlockedEventFragment.toNonNullUnlockedEvent()
            }
            node.fragments.unpinnedEventFragment != null -> {
                node.fragments.unpinnedEventFragment.toNonNullUnpinnedEvent()
            }
            // node.fragments.unsubscribedEventFragment != null -> {
            //     node.fragments.unsubscribedEventFragment.toNonNullUnsubscribedEvent()
            // }
            // node.fragments.userBlockedEventFragment != null -> {
            //     node.fragments.userBlockedEventFragment.toNonNullUserBlockedEvent()
            // }
            else -> {
                // unsupported type, just ignore it.
                null
            }
        }
    }

    private fun initTimelineItemWithRawData(node: PullRequestQuery.Node): PullRequestTimelineItem? {
        return when {
            node.fragments.addedToProjectEventFragment != null -> {
                node.fragments.addedToProjectEventFragment.toNonNullAddedToProjectEvent()
            }
            node.fragments.assignedEventFragment != null -> {
                node.fragments.assignedEventFragment.toNonNullAssignedEvent()
            }
            node.fragments.baseRefChangedEventFragment != null -> {
                node.fragments.baseRefChangedEventFragment.toNonNullBaseRefChangedEvent()
            }
            node.fragments.baseRefForcePushedEventFragment != null -> {
                node.fragments.baseRefForcePushedEventFragment.toNonNullBaseRefForcePushedEvent()
            }
            node.fragments.closedEventFragment != null -> {
                node.fragments.closedEventFragment.toNonNullClosedEvent()
            }
            // node.fragments.commentDeletedEventFragment != null -> {
            //     node.fragments.commentDeletedEventFragment.toNonNullCommentDeletedEvent()
            // }
            node.fragments.convertedNoteToIssueEventFragment != null -> {
                node.fragments.convertedNoteToIssueEventFragment.toNonNullConvertedNoteToIssueEvent()
            }
            node.fragments.crossReferencedEventFragment != null -> {
                node.fragments.crossReferencedEventFragment.toNonNullCrossReferencedEvent()
            }
            node.fragments.demilestonedEventFragment != null -> {
                node.fragments.demilestonedEventFragment.toNonNullDemilestonedEvent()
            }
            node.fragments.deployedEventFragment != null -> {
                node.fragments.deployedEventFragment.toNonNullDeployedEvent()
            }
            node.fragments.deploymentEnvironmentChangedEventFragment != null -> {
                node.fragments.deploymentEnvironmentChangedEventFragment.toNonNullDeploymentEnvironmentChangedEvent()
            }
            node.fragments.headRefDeletedEventFragment != null -> {
                node.fragments.headRefDeletedEventFragment.toNonNullHeadRefDeletedEvent()
            }
            node.fragments.headRefForcePushedEventFragment != null -> {
                node.fragments.headRefForcePushedEventFragment.toNonNullHeadRefForcePushedEvent()
            }
            node.fragments.headRefRestoredEventFragment != null -> {
                node.fragments.headRefRestoredEventFragment.toNonNullHeadRefRestoredEvent()
            }
            node.fragments.issueCommentFragment != null -> {
                node.fragments.issueCommentFragment.toNonNullIssueComment(owner, name)
            }
            node.fragments.labeledEventFragment != null -> {
                node.fragments.labeledEventFragment.toNonNullLabeledEvent()
            }
            node.fragments.lockedEventFragment != null -> {
                node.fragments.lockedEventFragment.toNonNullLockedEvent()
            }
            node.fragments.markedAsDuplicateEventFragment != null -> {
                node.fragments.markedAsDuplicateEventFragment.toNonNullMarkedAsDuplicateEvent()
            }
            // node.fragments.mentionedEventFragment != null -> {
            //     node.fragments.mentionedEventFragment?.toNonNullMentionedEvent()
            // }
            node.fragments.mergedEventFragment != null -> {
                node.fragments.mergedEventFragment.toNonNullMergedEvent()
            }
            node.fragments.milestonedEventFragment != null -> {
                node.fragments.milestonedEventFragment.toNonNullMilestonedEvent()
            }
            node.fragments.movedColumnsInProjectEventFragment != null -> {
                node.fragments.movedColumnsInProjectEventFragment.toNonNullMovedColumnsInProjectEvent()
            }
            node.fragments.pinnedEventFragment != null -> {
                node.fragments.pinnedEventFragment.toNonNullPinnedEvent()
            }
            node.fragments.pullRequestCommitFragment != null -> {
                node.fragments.pullRequestCommitFragment.toNonNullPullRequestCommit()
            }
            node.fragments.pullRequestCommitCommentThreadFragment != null -> {
                node.fragments.pullRequestCommitCommentThreadFragment.toNonNullPullRequestCommitCommentThread()
            }
            node.fragments.pullRequestReviewFragment != null -> {
                node.fragments.pullRequestReviewFragment.toNonNullPullRequestReview()
            }
            node.fragments.pullRequestReviewThreadFragment != null -> {
                node.fragments.pullRequestReviewThreadFragment.toNonNullPullRequestReviewThread()
            }
            // node.fragments.pullRequestRevisionMarkerFragment != null -> {
            //     node.fragments.pullRequestRevisionMarkerFragment.toNonNullPullRequestRevisionMarker()
            // }
            node.fragments.readyForReviewEventFragment != null -> {
                node.fragments.readyForReviewEventFragment.toNonNullReadyForReviewEvent()
            }
            node.fragments.referencedEventFragment != null -> {
                node.fragments.referencedEventFragment.toNonNullReferencedEvent()
            }
            node.fragments.removedFromProjectEventFragment != null -> {
                node.fragments.removedFromProjectEventFragment.toNonNullRemovedFromProjectEvent()
            }
            node.fragments.renamedTitleEventFragment != null -> {
                node.fragments.renamedTitleEventFragment.toNonNullRenamedTitleEvent()
            }
            node.fragments.reopenedEventFragment != null -> {
                node.fragments.reopenedEventFragment.toNonNullReopenedEvent()
            }
            node.fragments.reviewDismissedEventFragment != null -> {
                node.fragments.reviewDismissedEventFragment.toNonNullReviewDismissedEvent()
            }
            node.fragments.reviewRequestRemovedEventFragment != null -> {
                node.fragments.reviewRequestRemovedEventFragment.toNonNullReviewRequestRemovedEvent()
            }
            node.fragments.reviewRequestedEventFragment != null -> {
                node.fragments.reviewRequestedEventFragment.toNonNullReviewRequestedEvent()
            }
            // node.fragments.subscribedEventFragment != null -> {
            //     node.fragments.subscribedEventFragment.toNonNullSubscribedEvent()
            // }
            node.fragments.transferredEventFragment != null -> {
                node.fragments.transferredEventFragment.toNonNullTransferredEvent()
            }
            node.fragments.unassignedEventFragment != null -> {
                node.fragments.unassignedEventFragment.toNonNullUnassignedEvent()
            }
            node.fragments.unlabeledEventFragment != null -> {
                node.fragments.unlabeledEventFragment.toNonNullUnlabeledEvent()
            }
            node.fragments.unlockedEventFragment != null -> {
                node.fragments.unlockedEventFragment.toNonNullUnlockedEvent()
            }
            node.fragments.unpinnedEventFragment != null -> {
                node.fragments.unpinnedEventFragment.toNonNullUnpinnedEvent()
            }
            // node.fragments.unsubscribedEventFragment != null -> {
            //     node.fragments.unsubscribedEventFragment.toNonNullUnsubscribedEvent()
            // }
            // node.fragments.userBlockedEventFragment != null -> {
            //     node.fragments.userBlockedEventFragment.toNonNullUserBlockedEvent()
            // }
            else -> {
                // unsupported type, just ignore it.
                null
            }
        }
    }

}