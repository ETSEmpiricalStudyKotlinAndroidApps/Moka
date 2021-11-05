package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.PullRequest
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.data.toNullablePullRequest
import io.tonnyl.moka.graphql.PullRequestQuery
import io.tonnyl.moka.graphql.PullRequestTimelineItemsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

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
                            after = params.key,
                            before = params.key,
                            perPage = params.loadSize
                        )
                    ).execute().data?.repository?.pullRequest

                    pullRequestData.postValue(pullRequest.toNullablePullRequest())

                    val timeline = pullRequest?.timelineItems

                    list.addAll(
                        timeline?.nodes.orEmpty().mapNotNull { node ->
                            node?.let {
                                initTimelineItemWithRawData(node)
                            }
                        }
                    )

                    val pageInfo = timeline?.pageInfo?.pageInfo

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
                           after = params.key,
                           before = params.key,
                           perPage = params.loadSize
                       )
                    ).execute().data?.repository?.pullRequest?.timelineItems

                    list.addAll(
                        timeline?.nodes.orEmpty().mapNotNull { node ->
                            node?.let {
                                initTimelineItemWithRawData(it)
                            }
                        }
                    )

                    val pageInfo = timeline?.pageInfo?.pageInfo

                    LoadResult.Page(
                        data = list,
                        prevKey = pageInfo.checkedStartCursor,
                        nextKey = pageInfo.checkedEndCursor
                    )
                }
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<String, PullRequestTimelineItem>): String? {
        return null
    }

    private fun initTimelineItemWithRawData(node: PullRequestTimelineItemsQuery.Node): PullRequestTimelineItem? {
        return node.addedToProjectEventFragment?.toNonNullAddedToProjectEvent()
            ?: node.assignedEventFragment?.toNonNullAssignedEvent()
            ?: node.baseRefChangedEventFragment?.toNonNullBaseRefChangedEvent()
            ?: node.baseRefForcePushedEventFragment?.toNonNullBaseRefForcePushedEvent()
            ?: node.closedEventFragment?.toNonNullClosedEvent()
            ?: node.convertedNoteToIssueEventFragment?.toNonNullConvertedNoteToIssueEvent()
            ?: node.crossReferencedEventFragment?.toNonNullCrossReferencedEvent()
            ?: node.demilestonedEventFragment?.toNonNullDemilestonedEvent()
            ?: node.deployedEventFragment?.toNonNullDeployedEvent()
            ?: node.deploymentEnvironmentChangedEventFragment
                ?.toNonNullDeploymentEnvironmentChangedEvent()
            ?: node.headRefDeletedEventFragment?.toNonNullHeadRefDeletedEvent()
            ?: node.headRefForcePushedEventFragment?.toNonNullHeadRefForcePushedEvent()
            ?: node.headRefRestoredEventFragment?.toNonNullHeadRefRestoredEvent()
            ?: node.issueCommentFragment?.toNonNullIssueComment(owner, name)
            ?: node.labeledEventFragment?.toNonNullLabeledEvent()
            ?: node.lockedEventFragment?.toNonNullLockedEvent()
            ?: node.markedAsDuplicateEventFragment?.toNonNullMarkedAsDuplicateEvent()
            ?: node.mergedEventFragment?.toNonNullMergedEvent()
            ?: node.milestonedEventFragment?.toNonNullMilestonedEvent()
            ?: node.movedColumnsInProjectEventFragment?.toNonNullMovedColumnsInProjectEvent()
            ?: node.pinnedEventFragment?.toNonNullPinnedEvent()
            ?: node.pullRequestCommitFragment?.toNonNullPullRequestCommit()
            ?: node.pullRequestCommitCommentThreadFragment
                ?.toNonNullPullRequestCommitCommentThread()
            ?: node.pullRequestReviewFragment?.toNonNullPullRequestReview()
            ?: node.pullRequestReviewThreadFragment?.toNonNullPullRequestReviewThread()
            ?: node.readyForReviewEventFragment?.toNonNullReadyForReviewEvent()
            ?: node.referencedEventFragment?.toNonNullReferencedEvent()
            ?: node.removedFromProjectEventFragment?.toNonNullRemovedFromProjectEvent()
            ?: node.renamedTitleEventFragment?.toNonNullRenamedTitleEvent()
            ?: node.reopenedEventFragment?.toNonNullReopenedEvent()
            ?: node.reviewDismissedEventFragment?.toNonNullReviewDismissedEvent()
            ?: node.reviewRequestRemovedEventFragment?.toNonNullReviewRequestRemovedEvent()
            ?: node.reviewRequestedEventFragment?.toNonNullReviewRequestedEvent()
            ?: node.transferredEventFragment?.toNonNullTransferredEvent()
            ?: node.unassignedEventFragment?.toNonNullUnassignedEvent()
            ?: node.unlabeledEventFragment?.toNonNullUnlabeledEvent()
            ?: node.unlockedEventFragment?.toNonNullUnlockedEvent()
            ?: node.unpinnedEventFragment?.toNonNullUnpinnedEvent()
    }

    private fun initTimelineItemWithRawData(node: PullRequestQuery.Node): PullRequestTimelineItem? {
        return node.addedToProjectEventFragment?.toNonNullAddedToProjectEvent()
            ?: node.assignedEventFragment?.toNonNullAssignedEvent()
            ?: node.baseRefChangedEventFragment?.toNonNullBaseRefChangedEvent()
            ?: node.baseRefForcePushedEventFragment?.toNonNullBaseRefForcePushedEvent()
            ?: node.closedEventFragment?.toNonNullClosedEvent()
            ?: node.convertedNoteToIssueEventFragment?.toNonNullConvertedNoteToIssueEvent()
            ?: node.crossReferencedEventFragment?.toNonNullCrossReferencedEvent()
            ?: node.demilestonedEventFragment?.toNonNullDemilestonedEvent()
            ?: node.deployedEventFragment?.toNonNullDeployedEvent()
            ?: node.deploymentEnvironmentChangedEventFragment
                ?.toNonNullDeploymentEnvironmentChangedEvent()
            ?: node.headRefDeletedEventFragment?.toNonNullHeadRefDeletedEvent()
            ?: node.headRefForcePushedEventFragment?.toNonNullHeadRefForcePushedEvent()
            ?: node.headRefRestoredEventFragment?.toNonNullHeadRefRestoredEvent()
            ?: node.issueCommentFragment?.toNonNullIssueComment(owner, name)
            ?: node.labeledEventFragment?.toNonNullLabeledEvent()
            ?: node.lockedEventFragment?.toNonNullLockedEvent()
            ?: node.markedAsDuplicateEventFragment?.toNonNullMarkedAsDuplicateEvent()
            ?: node.mergedEventFragment?.toNonNullMergedEvent()
            ?: node.milestonedEventFragment?.toNonNullMilestonedEvent()
            ?: node.movedColumnsInProjectEventFragment?.toNonNullMovedColumnsInProjectEvent()
            ?: node.pinnedEventFragment?.toNonNullPinnedEvent()
            ?: node.pullRequestCommitFragment?.toNonNullPullRequestCommit()
            ?: node.pullRequestCommitCommentThreadFragment
                ?.toNonNullPullRequestCommitCommentThread()
            ?: node.pullRequestReviewFragment?.toNonNullPullRequestReview()
            ?: node.pullRequestReviewThreadFragment?.toNonNullPullRequestReviewThread()
            ?: node.readyForReviewEventFragment?.toNonNullReadyForReviewEvent()
            ?: node.referencedEventFragment?.toNonNullReferencedEvent()
            ?: node.removedFromProjectEventFragment?.toNonNullRemovedFromProjectEvent()
            ?: node.renamedTitleEventFragment?.toNonNullRenamedTitleEvent()
            ?: node.reopenedEventFragment?.toNonNullReopenedEvent()
            ?: node.reviewDismissedEventFragment?.toNonNullReviewDismissedEvent()
            ?: node.reviewRequestRemovedEventFragment?.toNonNullReviewRequestRemovedEvent()
            ?: node.reviewRequestedEventFragment?.toNonNullReviewRequestedEvent()
            ?: node.transferredEventFragment?.toNonNullTransferredEvent()
            ?: node.unassignedEventFragment?.toNonNullUnassignedEvent()
            ?: node.unlabeledEventFragment?.toNonNullUnlabeledEvent()
            ?: node.unlockedEventFragment?.toNonNullUnlockedEvent()
            ?: node.unpinnedEventFragment?.toNonNullUnpinnedEvent()
    }

}