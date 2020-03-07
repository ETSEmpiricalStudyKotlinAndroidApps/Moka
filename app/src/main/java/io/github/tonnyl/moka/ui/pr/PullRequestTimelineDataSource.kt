package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class PullRequestTimelineDataSource(
    private val owner: String,
    private val name: String,
    private val number: Int,
    private val initialLoadStatus: MutableLiveData<Resource<List<PullRequestTimelineItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource2<List<PullRequestTimelineItem>>>
) : PageKeyedDataSource<String, PullRequestTimelineItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, PullRequestTimelineItem>
    ) {
        Timber.d("loadInitial")

        initialLoadStatus.postValue(Resource.loading(null))

        try {
            val pullRequestTimelineQuery = PullRequestTimelineItemsQuery(
                owner,
                name,
                number,
                Input.absent(),
                Input.absent(),
                params.requestedLoadSize
            )

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(pullRequestTimelineQuery)
                    .execute()
            }

            val list = mutableListOf<PullRequestTimelineItem>()
            val timeline = response.data()?.repository?.pullRequest?.timelineItems

            timeline?.nodes?.forEach { node ->
                node?.let {
                    initTimelineItemWithRawData(node)?.let {
                        list.add(it)
                    }
                }
            }

            val pageInfo = timeline?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                if (pageInfo?.hasPreviousPage == true) {
                    pageInfo.startCursor
                } else {
                    null
                },
                if (pageInfo?.hasNextPage == true) {
                    pageInfo.endCursor
                } else {
                    null
                }
            )

            retry = null

            initialLoadStatus.postValue(Resource.success(list))
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadInitial(params, callback)
            }

            initialLoadStatus.postValue(Resource.error(e.message, null))
        }
    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, PullRequestTimelineItem>
    ) {
        Timber.d("loadAfter")

        pagedLoadStatus.postValue(
            PagedResource2(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val timelineQuery = PullRequestTimelineItemsQuery(
                owner,
                name,
                number,
                Input.fromNullable(params.key),
                Input.absent(),
                params.requestedLoadSize
            )

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(timelineQuery)
                    .execute()
            }

            val list = mutableListOf<PullRequestTimelineItem>()
            val timeline = response.data()?.repository?.pullRequest?.timelineItems

            timeline?.nodes?.forEach { node ->
                node?.let {
                    initTimelineItemWithRawData(node)?.let {
                        list.add(it)
                    }
                }
            }

            val pageInfo = timeline?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                if (pageInfo?.hasNextPage == true) {
                    pageInfo.endCursor
                } else {
                    null
                }
            )

            retry = null

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.AFTER, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadAfter(params, callback)
            }

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.AFTER, Resource.error(e.message, null))
            )
        }
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, PullRequestTimelineItem>
    ) {
        Timber.d("loadBefore")

        pagedLoadStatus.postValue(
            PagedResource2(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val timelineQuery = PullRequestTimelineItemsQuery(
                owner,
                name,
                number,
                Input.absent(),
                Input.fromNullable(params.key),
                params.requestedLoadSize
            )

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(timelineQuery)
                    .execute()
            }

            val list = mutableListOf<PullRequestTimelineItem>()
            val timeline = response.data()?.repository?.pullRequest?.timelineItems

            timeline?.nodes?.forEach { node ->
                node?.let {
                    initTimelineItemWithRawData(node)?.let {
                        list.add(it)
                    }
                }
            }

            val pageInfo = timeline?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                if (pageInfo?.hasPreviousPage == true) {
                    pageInfo.startCursor
                } else {
                    null
                }
            )

            retry = null

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.BEFORE, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadBefore(params, callback)
            }

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.BEFORE, Resource.error(e.message, null))
            )
        }
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

}