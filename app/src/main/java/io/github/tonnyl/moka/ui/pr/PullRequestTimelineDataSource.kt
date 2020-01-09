package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.fragment.*
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
        return when (node.__typename) {
            AddedToProjectEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.addedToProjectEventFragment?.toNonNullAddedToProjectEvent()
            }
            AssignedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.assignedEventFragment?.toNonNullAssignedEvent()
            }
            BaseRefChangedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.baseRefChangedEventFragment?.toNonNullBaseRefChangedEvent()
            }
            BaseRefForcePushedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.baseRefForcePushedEventFragment?.toNonNullBaseRefForcePushedEvent()
            }
            ClosedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.closedEventFragment?.toNonNullClosedEvent()
            }
            // CommentDeletedEventFragment.POSSIBLE_TYPES.first() -> {
            //     node.fragments().commentDeletedEventFragment()?.toNonNullCommentDeletedEvent()
            // }
            ConvertedNoteToIssueEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.convertedNoteToIssueEventFragment?.toNonNullConvertedNoteToIssueEvent()
            }
            CrossReferencedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.crossReferencedEventFragment?.toNonNullCrossReferencedEvent()
            }
            DemilestonedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.demilestonedEventFragment?.toNonNullDemilestonedEvent()
            }
            DeployedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.deployedEventFragment?.toNonNullDeployedEvent()
            }
            DeploymentEnvironmentChangedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.deploymentEnvironmentChangedEventFragment?.toNonNullDeploymentEnvironmentChangedEvent()
            }
            HeadRefDeletedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.headRefDeletedEventFragment?.toNonNullHeadRefDeletedEvent()
            }
            HeadRefForcePushedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.headRefForcePushedEventFragment?.toNonNullHeadRefForcePushedEvent()
            }
            HeadRefRestoredEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.headRefRestoredEventFragment?.toNonNullHeadRefRestoredEvent()
            }
            IssueCommentFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.issueCommentFragment?.toNonNullIssueComment()
            }
            LabeledEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.labeledEventFragment?.toNonNullLabeledEvent()
            }
            LockedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.lockedEventFragment?.toNonNullLockedEvent()
            }
            MarkedAsDuplicateEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.markedAsDuplicateEventFragment?.toNonNullMarkedAsDuplicateEvent()
            }
            // MentionedEventFragment.POSSIBLE_TYPES.first() -> {
            //     node.fragments().mentionedEventFragment()?.toNonNullMentionedEvent()
            // }
            MergedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.mergedEventFragment?.toNonNullMergedEvent()
            }
            MilestonedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.milestonedEventFragment?.toNonNullMilestonedEvent()
            }
            MovedColumnsInProjectEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.movedColumnsInProjectEventFragment?.toNonNullMovedColumnsInProjectEvent()
            }
            PinnedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.pinnedEventFragment?.toNonNullPinnedEvent()
            }
            PullRequestCommitFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.pullRequestCommitFragment?.toNonNullPullRequestCommit()
            }
            PullRequestCommitCommentThreadFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.pullRequestCommitCommentThreadFragment?.toNonNullPullRequestCommitCommentThread()
            }
            PullRequestReviewFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.pullRequestReviewFragment?.toNonNullPullRequestReview()
            }
            PullRequestReviewThreadFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.pullRequestReviewThreadFragment?.toNonNullPullRequestReviewThread()
            }
            // PullRequestRevisionMarkerFragment.POSSIBLE_TYPES.first() -> {
            //     node.fragments()
            //         .pullRequestRevisionMarkerFragment()
            //         ?.toNonNullPullRequestRevisionMarker()
            // }
            ReadyForReviewEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.readyForReviewEventFragment?.toNonNullReadyForReviewEvent()
            }
            ReferencedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.referencedEventFragment?.toNonNullReferencedEvent()
            }
            RemovedFromProjectEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.removedFromProjectEventFragment?.toNonNullRemovedFromProjectEvent()
            }
            RenamedTitleEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.renamedTitleEventFragment?.toNonNullRenamedTitleEvent()
            }
            ReopenedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.reopenedEventFragment?.toNonNullReopenedEvent()
            }
            ReviewDismissedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.reviewDismissedEventFragment?.toNonNullReviewDismissedEvent()
            }
            ReviewRequestRemovedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.reviewRequestRemovedEventFragment?.toNonNullReviewRequestRemovedEvent()
            }
            ReviewRequestedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.reviewRequestedEventFragment?.toNonNullReviewRequestedEvent()
            }
            // SubscribedEventFragment.POSSIBLE_TYPES.first() -> {
            //     node.fragments().subscribedEventFragment()?.toNonNullSubscribedEvent()
            // }
            TransferredEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.transferredEventFragment?.toNonNullTransferredEvent()
            }
            UnassignedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.unassignedEventFragment?.toNonNullUnassignedEvent()
            }
            UnlabeledEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.unlabeledEventFragment?.toNonNullUnlabeledEvent()
            }
            UnlockedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.unlockedEventFragment?.toNonNullUnlockedEvent()
            }
            UnpinnedEventFragment.POSSIBLE_TYPES.first() -> {
                node.fragments.unpinnedEventFragment?.toNonNullUnpinnedEvent()
            }
            // UnsubscribedEventFragment.POSSIBLE_TYPES.first() -> {
            //     node.fragments().unsubscribedEventFragment()?.toNonNullUnsubscribedEvent()
            // }
            // UserBlockedEventFragment.POSSIBLE_TYPES.first() -> {
            //     node.fragments().userBlockedEventFragment()?.toNonNullUserBlockedEvent()
            // }
            else -> {
                // unsupported type, just ignore it.
                null
            }
        }
    }

}