package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import io.tonnyl.moka.common.data.PullRequestTimelineItem
import io.tonnyl.moka.common.data.extension.checkedEndCursor
import io.tonnyl.moka.common.data.extension.checkedStartCursor
import io.tonnyl.moka.graphql.PullRequestQuery
import io.tonnyl.moka.graphql.PullRequestQuery.PullRequest
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
                            after = Optional.presentIfNotNull(params.key),
                            before = Optional.presentIfNotNull(params.key),
                            perPage = params.loadSize
                        )
                    ).execute().data?.repository?.pullRequest

                    pullRequest?.let {
                        pullRequestData.postValue(it)
                    }

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
                        query = PullRequestTimelineItemsQuery(
                            owner = owner,
                            name = name,
                            number = number,
                            after = Optional.presentIfNotNull(params.key),
                            before = Optional.presentIfNotNull(params.key),
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

    private fun initTimelineItemWithRawData(node: PullRequestTimelineItemsQuery.Node): PullRequestTimelineItem {
        return PullRequestTimelineItem(
            addedToProjectEvent = node.addedToProjectEventFragment,
            assignedEvent = node.assignedEventFragment,
            baseRefChangedEvent = node.baseRefChangedEventFragment,
            baseRefForcePushedEvent = node.baseRefForcePushedEventFragment,
            closedEvent = node.closedEventFragment,
            convertedNoteToIssueEvent = node.convertedNoteToIssueEventFragment,
            crossReferencedEvent = node.crossReferencedEventFragment,
            demilestonedEvent = node.demilestonedEventFragment,
            deployedEvent = node.deployedEventFragment,
            deploymentEnvironmentChangedEvent = node.deploymentEnvironmentChangedEventFragment,
            headRefDeletedEvent = node.headRefDeletedEventFragment,
            headRefForcePushedEvent = node.headRefForcePushedEventFragment,
            headRefRestoredEvent = node.headRefRestoredEventFragment,
            issueComment = node.issueCommentFragment,
            labeledEvent = node.labeledEventFragment,
            lockedEvent = node.lockedEventFragment,
            markedAsDuplicateEvent = node.markedAsDuplicateEventFragment,
            mergedEvent = node.mergedEventFragment,
            milestonedEvent = node.milestonedEventFragment,
            movedColumnsInProjectEvent = node.movedColumnsInProjectEventFragment,
            pinnedEvent = node.pinnedEventFragment,
            pullRequestCommit = node.pullRequestCommitFragment,
            pullRequestCommitCommentThread = node.pullRequestCommitCommentThreadFragment,
            pullRequestReview = node.pullRequestReviewFragment,
            pullRequestReviewThread = node.pullRequestReviewThreadFragment,
            readyForReviewEvent = node.readyForReviewEventFragment,
            referencedEvent = node.referencedEventFragment,
            removedFromProjectEvent = node.removedFromProjectEventFragment,
            renamedTitleEvent = node.renamedTitleEventFragment,
            reopenedEvent = node.reopenedEventFragment,
            reviewDismissedEvent = node.reviewDismissedEventFragment,
            reviewRequestRemovedEvent = node.reviewRequestRemovedEventFragment,
            reviewRequestedEvent = node.reviewRequestedEventFragment,
            transferredEvent = node.transferredEventFragment,
            unassignedEvent = node.unassignedEventFragment,
            unlabeledEvent = node.unlabeledEventFragment,
            unlockedEvent = node.unlockedEventFragment,
            unpinnedEvent = node.unpinnedEventFragment
        )
    }

    private fun initTimelineItemWithRawData(node: PullRequestQuery.Node): PullRequestTimelineItem {
        return PullRequestTimelineItem(
            addedToProjectEvent = node.addedToProjectEventFragment,
            assignedEvent = node.assignedEventFragment,
            baseRefChangedEvent = node.baseRefChangedEventFragment,
            baseRefForcePushedEvent = node.baseRefForcePushedEventFragment,
            closedEvent = node.closedEventFragment,
            convertedNoteToIssueEvent = node.convertedNoteToIssueEventFragment,
            crossReferencedEvent = node.crossReferencedEventFragment,
            demilestonedEvent = node.demilestonedEventFragment,
            deployedEvent = node.deployedEventFragment,
            deploymentEnvironmentChangedEvent = node.deploymentEnvironmentChangedEventFragment,
            headRefDeletedEvent = node.headRefDeletedEventFragment,
            headRefForcePushedEvent = node.headRefForcePushedEventFragment,
            headRefRestoredEvent = node.headRefRestoredEventFragment,
            issueComment = node.issueCommentFragment,
            labeledEvent = node.labeledEventFragment,
            lockedEvent = node.lockedEventFragment,
            markedAsDuplicateEvent = node.markedAsDuplicateEventFragment,
            mergedEvent = node.mergedEventFragment,
            milestonedEvent = node.milestonedEventFragment,
            movedColumnsInProjectEvent = node.movedColumnsInProjectEventFragment,
            pinnedEvent = node.pinnedEventFragment,
            pullRequestCommit = node.pullRequestCommitFragment,
            pullRequestCommitCommentThread = node.pullRequestCommitCommentThreadFragment,
            pullRequestReview = node.pullRequestReviewFragment,
            pullRequestReviewThread = node.pullRequestReviewThreadFragment,
            readyForReviewEvent = node.readyForReviewEventFragment,
            referencedEvent = node.referencedEventFragment,
            removedFromProjectEvent = node.removedFromProjectEventFragment,
            renamedTitleEvent = node.renamedTitleEventFragment,
            reopenedEvent = node.reopenedEventFragment,
            reviewDismissedEvent = node.reviewDismissedEventFragment,
            reviewRequestRemovedEvent = node.reviewRequestRemovedEventFragment,
            reviewRequestedEvent = node.reviewRequestedEventFragment,
            transferredEvent = node.transferredEventFragment,
            unassignedEvent = node.unassignedEventFragment,
            unlabeledEvent = node.unlabeledEventFragment,
            unlockedEvent = node.unlockedEventFragment,
            unpinnedEvent = node.unpinnedEventFragment
        )
    }

}