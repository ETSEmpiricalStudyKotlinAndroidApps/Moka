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
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.addedToProjectEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.assignedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.baseRefChangedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.baseRefForcePushedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.closedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.convertedNoteToIssueEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.crossReferencedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.demilestonedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.deployedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.deploymentEnvironmentChangedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.headRefDeletedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.headRefForcePushedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.headRefRestoredEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.issueCommentFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.labeledEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.lockedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.markedAsDuplicateEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.mergedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.milestonedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.movedColumnsInProjectEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.pinnedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.pullRequestCommitCommentThreadFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.pullRequestCommitFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.pullRequestReviewFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.pullRequestReviewThreadFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.readyForReviewEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.referencedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.removedFromProjectEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.renamedTitleEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.reopenedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.reviewDismissedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.reviewRequestRemovedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.reviewRequestedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.transferredEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.unassignedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.unlabeledEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.unlockedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.unpinnedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.PageInfo.Companion.pageInfo
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.addedToProjectEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.assignedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.baseRefChangedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.baseRefForcePushedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.closedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.convertedNoteToIssueEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.crossReferencedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.demilestonedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.deployedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.deploymentEnvironmentChangedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.headRefDeletedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.headRefForcePushedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.headRefRestoredEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.issueCommentFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.labeledEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.lockedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.markedAsDuplicateEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.mergedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.milestonedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.movedColumnsInProjectEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.pinnedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.pullRequestCommitCommentThreadFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.pullRequestCommitFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.pullRequestReviewFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.pullRequestReviewThreadFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.readyForReviewEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.referencedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.removedFromProjectEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.renamedTitleEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.reopenedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.reviewDismissedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.reviewRequestRemovedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.reviewRequestedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.transferredEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.unassignedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.unlabeledEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.unlockedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes.Companion.unpinnedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.PageInfo.Companion.pageInfo
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
                    ).data?.repository?.pullRequest

                    pullRequestData.postValue(pullRequest.toNullablePullRequest())

                    val timeline = pullRequest?.timelineItems

                    list.addAll(
                        timeline?.nodes.orEmpty().mapNotNull { node ->
                            node?.let {
                                initTimelineItemWithRawData(node)
                            }
                        }
                    )

                    val pageInfo = timeline?.pageInfo?.pageInfo()

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
                    ).data?.repository?.pullRequest?.timelineItems

                    list.addAll(
                        timeline?.nodes.orEmpty().mapNotNull { node ->
                            node?.let {
                                initTimelineItemWithRawData(it)
                            }
                        }
                    )

                    val pageInfo = timeline?.pageInfo?.pageInfo()

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

    private fun initTimelineItemWithRawData(node: PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Nodes): PullRequestTimelineItem? {
        return node.addedToProjectEventFragment()?.toNonNullAddedToProjectEvent()
            ?: node.assignedEventFragment()?.toNonNullAssignedEvent()
            ?: node.baseRefChangedEventFragment()?.toNonNullBaseRefChangedEvent()
            ?: node.baseRefForcePushedEventFragment()?.toNonNullBaseRefForcePushedEvent()
            ?: node.closedEventFragment()?.toNonNullClosedEvent()
            ?: node.convertedNoteToIssueEventFragment()?.toNonNullConvertedNoteToIssueEvent()
            ?: node.crossReferencedEventFragment()?.toNonNullCrossReferencedEvent()
            ?: node.demilestonedEventFragment()?.toNonNullDemilestonedEvent()
            ?: node.deployedEventFragment()?.toNonNullDeployedEvent()
            ?: node.deploymentEnvironmentChangedEventFragment()
                ?.toNonNullDeploymentEnvironmentChangedEvent()
            ?: node.headRefDeletedEventFragment()?.toNonNullHeadRefDeletedEvent()
            ?: node.headRefForcePushedEventFragment()?.toNonNullHeadRefForcePushedEvent()
            ?: node.headRefRestoredEventFragment()?.toNonNullHeadRefRestoredEvent()
            ?: node.issueCommentFragment()?.toNonNullIssueComment(owner, name)
            ?: node.labeledEventFragment()?.toNonNullLabeledEvent()
            ?: node.lockedEventFragment()?.toNonNullLockedEvent()
            ?: node.markedAsDuplicateEventFragment()?.toNonNullMarkedAsDuplicateEvent()
            ?: node.mergedEventFragment()?.toNonNullMergedEvent()
            ?: node.milestonedEventFragment()?.toNonNullMilestonedEvent()
            ?: node.movedColumnsInProjectEventFragment()?.toNonNullMovedColumnsInProjectEvent()
            ?: node.pinnedEventFragment()?.toNonNullPinnedEvent()
            ?: node.pullRequestCommitFragment()?.toNonNullPullRequestCommit()
            ?: node.pullRequestCommitCommentThreadFragment()
                ?.toNonNullPullRequestCommitCommentThread()
            ?: node.pullRequestReviewFragment()?.toNonNullPullRequestReview()
            ?: node.pullRequestReviewThreadFragment()?.toNonNullPullRequestReviewThread()
            ?: node.readyForReviewEventFragment()?.toNonNullReadyForReviewEvent()
            ?: node.referencedEventFragment()?.toNonNullReferencedEvent()
            ?: node.removedFromProjectEventFragment()?.toNonNullRemovedFromProjectEvent()
            ?: node.renamedTitleEventFragment()?.toNonNullRenamedTitleEvent()
            ?: node.reopenedEventFragment()?.toNonNullReopenedEvent()
            ?: node.reviewDismissedEventFragment()?.toNonNullReviewDismissedEvent()
            ?: node.reviewRequestRemovedEventFragment()?.toNonNullReviewRequestRemovedEvent()
            ?: node.reviewRequestedEventFragment()?.toNonNullReviewRequestedEvent()
            ?: node.transferredEventFragment()?.toNonNullTransferredEvent()
            ?: node.unassignedEventFragment()?.toNonNullUnassignedEvent()
            ?: node.unlabeledEventFragment()?.toNonNullUnlabeledEvent()
            ?: node.unlockedEventFragment()?.toNonNullUnlockedEvent()
            ?: node.unpinnedEventFragment()?.toNonNullUnpinnedEvent()
    }

    private fun initTimelineItemWithRawData(node: PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Nodes): PullRequestTimelineItem? {
        return node.addedToProjectEventFragment()?.toNonNullAddedToProjectEvent()
            ?: node.assignedEventFragment()?.toNonNullAssignedEvent()
            ?: node.baseRefChangedEventFragment()?.toNonNullBaseRefChangedEvent()
            ?: node.baseRefForcePushedEventFragment()?.toNonNullBaseRefForcePushedEvent()
            ?: node.closedEventFragment()?.toNonNullClosedEvent()
            ?: node.convertedNoteToIssueEventFragment()?.toNonNullConvertedNoteToIssueEvent()
            ?: node.crossReferencedEventFragment()?.toNonNullCrossReferencedEvent()
            ?: node.demilestonedEventFragment()?.toNonNullDemilestonedEvent()
            ?: node.deployedEventFragment()?.toNonNullDeployedEvent()
            ?: node.deploymentEnvironmentChangedEventFragment()
                ?.toNonNullDeploymentEnvironmentChangedEvent()
            ?: node.headRefDeletedEventFragment()?.toNonNullHeadRefDeletedEvent()
            ?: node.headRefForcePushedEventFragment()?.toNonNullHeadRefForcePushedEvent()
            ?: node.headRefRestoredEventFragment()?.toNonNullHeadRefRestoredEvent()
            ?: node.issueCommentFragment()?.toNonNullIssueComment(owner, name)
            ?: node.labeledEventFragment()?.toNonNullLabeledEvent()
            ?: node.lockedEventFragment()?.toNonNullLockedEvent()
            ?: node.markedAsDuplicateEventFragment()?.toNonNullMarkedAsDuplicateEvent()
            ?: node.mergedEventFragment()?.toNonNullMergedEvent()
            ?: node.milestonedEventFragment()?.toNonNullMilestonedEvent()
            ?: node.movedColumnsInProjectEventFragment()?.toNonNullMovedColumnsInProjectEvent()
            ?: node.pinnedEventFragment()?.toNonNullPinnedEvent()
            ?: node.pullRequestCommitFragment()?.toNonNullPullRequestCommit()
            ?: node.pullRequestCommitCommentThreadFragment()
                ?.toNonNullPullRequestCommitCommentThread()
            ?: node.pullRequestReviewFragment()?.toNonNullPullRequestReview()
            ?: node.pullRequestReviewThreadFragment()?.toNonNullPullRequestReviewThread()
            ?: node.readyForReviewEventFragment()?.toNonNullReadyForReviewEvent()
            ?: node.referencedEventFragment()?.toNonNullReferencedEvent()
            ?: node.removedFromProjectEventFragment()?.toNonNullRemovedFromProjectEvent()
            ?: node.renamedTitleEventFragment()?.toNonNullRenamedTitleEvent()
            ?: node.reopenedEventFragment()?.toNonNullReopenedEvent()
            ?: node.reviewDismissedEventFragment()?.toNonNullReviewDismissedEvent()
            ?: node.reviewRequestRemovedEventFragment()?.toNonNullReviewRequestRemovedEvent()
            ?: node.reviewRequestedEventFragment()?.toNonNullReviewRequestedEvent()
            ?: node.transferredEventFragment()?.toNonNullTransferredEvent()
            ?: node.unassignedEventFragment()?.toNonNullUnassignedEvent()
            ?: node.unlabeledEventFragment()?.toNonNullUnlabeledEvent()
            ?: node.unlockedEventFragment()?.toNonNullUnlockedEvent()
            ?: node.unpinnedEventFragment()?.toNonNullUnpinnedEvent()
    }

}