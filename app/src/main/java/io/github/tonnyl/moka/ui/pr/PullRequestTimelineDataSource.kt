package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.data.PullRequest
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.data.toNullablePullRequest
import io.github.tonnyl.moka.queries.PullRequestQuery
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.addedToProjectEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.assignedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.baseRefChangedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.baseRefForcePushedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.closedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.convertedNoteToIssueEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.crossReferencedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.demilestonedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.deployedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.deploymentEnvironmentChangedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.headRefDeletedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.headRefForcePushedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.headRefRestoredEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.issueCommentFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.labeledEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.lockedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.markedAsDuplicateEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.mergedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.milestonedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.movedColumnsInProjectEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.pinnedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.pullRequestCommitCommentThreadFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.pullRequestCommitFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.pullRequestReviewFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.pullRequestReviewThreadFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.readyForReviewEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.referencedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.removedFromProjectEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.renamedTitleEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.reopenedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.reviewDismissedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.reviewRequestRemovedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.reviewRequestedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.transferredEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.unassignedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.unlabeledEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.unlockedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.unpinnedEventFragment
import io.github.tonnyl.moka.queries.PullRequestQuery.Data.Repository.PullRequest.TimelineItems.PageInfo.Companion.pageInfo
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.addedToProjectEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.assignedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.baseRefChangedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.baseRefForcePushedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.closedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.convertedNoteToIssueEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.crossReferencedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.demilestonedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.deployedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.deploymentEnvironmentChangedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.headRefDeletedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.headRefForcePushedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.headRefRestoredEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.issueCommentFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.labeledEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.lockedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.markedAsDuplicateEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.mergedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.milestonedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.movedColumnsInProjectEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.pinnedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.pullRequestCommitCommentThreadFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.pullRequestCommitFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.pullRequestReviewFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.pullRequestReviewThreadFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.readyForReviewEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.referencedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.removedFromProjectEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.renamedTitleEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.reopenedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.reviewDismissedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.reviewRequestRemovedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.reviewRequestedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.transferredEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.unassignedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.unlabeledEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.unlockedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node.Companion.unpinnedEventFragment
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.PageInfo.Companion.pageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PullRequestTimelineDataSource(
    private val apolloClient: ApolloClient,
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
                    val pullRequest = apolloClient.query(
                        query = PullRequestQuery(
                            owner = owner,
                            name = name,
                            number = number,
                            after = Input.Present(value = params.key),
                            before = Input.Present(value = params.key),
                            perPage = params.loadSize
                        )
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
                    val timeline = apolloClient.query(
                       query =  PullRequestTimelineItemsQuery(
                            owner = owner,
                            name = name,
                            number = number,
                            after = Input.Present(value = params.key),
                            before = Input.Present(value = params.key),
                            perPage = params.loadSize
                        )
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

    private fun initTimelineItemWithRawData(node: PullRequestTimelineItemsQuery.Data.Repository.PullRequest.TimelineItems.Node): PullRequestTimelineItem? {
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

    private fun initTimelineItemWithRawData(node: PullRequestQuery.Data.Repository.PullRequest.TimelineItems.Node): PullRequestTimelineItem? {
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