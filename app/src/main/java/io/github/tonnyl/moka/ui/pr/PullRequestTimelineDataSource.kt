package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.PullRequestTimelineQuery
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.network.NetworkClient
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
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
            val pullRequestTimelineQuery = PullRequestTimelineQuery.builder()
                .owner(owner)
                .name(name)
                .number(number)
                .perPage(params.requestedLoadSize)
                .build()

            val response = runBlocking {
                NetworkClient.apolloClient
                    .query(pullRequestTimelineQuery)
                    .execute()
            }

            val list = mutableListOf<PullRequestTimelineItem>()
            val timeline = response.data()?.repository()?.pullRequest()?.timelineItems()

            timeline?.nodes()?.forEach { node ->
                initTimelineItemWithRawData(node)?.let {
                    list.add(it)
                }
            }

            val pageInfo = timeline?.pageInfo()
            callback.onResult(
                list,
                if (pageInfo?.hasPreviousPage() == true) {
                    timeline.pageInfo().startCursor()
                } else {
                    null
                },
                if (pageInfo?.hasNextPage() == true) {
                    timeline.pageInfo().endCursor()
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
            val timelineQuery = PullRequestTimelineQuery.builder()
                .owner(owner)
                .name(name)
                .number(number)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

            val response = runBlocking {
                NetworkClient.apolloClient
                    .query(timelineQuery)
                    .execute()
            }

            val list = mutableListOf<PullRequestTimelineItem>()
            val timeline = response.data()?.repository()?.pullRequest()?.timelineItems()

            timeline?.nodes()?.forEach { node ->
                initTimelineItemWithRawData(node)?.let {
                    list.add(it)
                }
            }

            callback.onResult(
                list,
                if (timeline?.pageInfo()?.hasNextPage() == true) {
                    timeline.pageInfo().endCursor()
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
            val timelineQuery = PullRequestTimelineQuery.builder()
                .owner(owner)
                .name(name)
                .number(number)
                .perPage(params.requestedLoadSize)
                .before(params.key)
                .build()

            val response = runBlocking {
                NetworkClient.apolloClient
                    .query(timelineQuery)
                    .execute()
            }

            val list = mutableListOf<PullRequestTimelineItem>()
            val timeline = response.data()?.repository()?.pullRequest()?.timelineItems()

            timeline?.nodes()?.forEach { node ->
                initTimelineItemWithRawData(node)?.let {
                    list.add(it)
                }
            }

            callback.onResult(
                list,
                if (timeline?.pageInfo()?.hasPreviousPage() == true) {
                    timeline.pageInfo().startCursor()
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

    private fun initTimelineItemWithRawData(node: PullRequestTimelineQuery.Node): PullRequestTimelineItem? {
        return when {
            node.fragments().assignedEventFragment() != null -> {
                PullRequestAssignedEvent.createFromRaw(node.fragments().assignedEventFragment())
            }
            node.fragments().baseRefForcePushedEventFragment() != null -> {
                PullRequestBaseRefForcePushedEvent.createFromRaw(node.fragments().baseRefForcePushedEventFragment())
            }
            node.fragments().closedEventFragment() != null -> {
                PullRequestClosedEvent.createFromRaw(node.fragments().closedEventFragment())
            }
//            node.fragments().commitEventFragment() != null -> {
//                PullRequestCommitEvent.createFromRaw(node.fragments().commitEventFragment())
//            }
            node.fragments().crossReferencedEventFragment() != null -> {
                PullRequestCrossReferencedEvent.createFromRaw(node.fragments().crossReferencedEventFragment())
            }
            node.fragments().demilestonedEventFragment() != null -> {
                PullRequestDemilestonedEvent.createFromRaw(node.fragments().demilestonedEventFragment())
            }
            node.fragments().deployedEventFragment() != null -> {
                PullRequestDeployedEvent.createFromRaw(node.fragments().deployedEventFragment())
            }
            node.fragments().deploymentEnvironmentChangedEventFragment() != null -> {
                PullRequestDeploymentEnvironmentChangedEvent.createFromRaw(node.fragments().deploymentEnvironmentChangedEventFragment())
            }
            node.fragments().headRefDeletedEventFragment() != null -> {
                PullRequestHeadRefDeletedEvent.createFromRaw(node.fragments().headRefDeletedEventFragment())
            }
            node.fragments().headRefForcePushedEventFragment() != null -> {
                PullRequestHeadRefForcePushedEvent.createFromRaw(node.fragments().headRefForcePushedEventFragment())
            }
            node.fragments().headRefRestoredEventFragment() != null -> {
                PullRequestHeadRefRestoredEvent.createFromRaw(node.fragments().headRefRestoredEventFragment())
            }
            node.fragments().issueCommentFragment() != null -> {
                PullRequestIssueComment.createFromRaw(node.fragments().issueCommentFragment())
            }
            node.fragments().labeledEventFragment() != null -> {
                PullRequestLabeledEvent.createFromRaw(node.fragments().labeledEventFragment())
            }
            node.fragments().lockedEventFragment() != null -> {
                PullRequestLockedEvent.createFromRaw(node.fragments().lockedEventFragment())
            }
            node.fragments().mergedEventFragment() != null -> {
                PullRequestMergedEvent.createFromRaw(node.fragments().mergedEventFragment())
            }
            node.fragments().milestonedEventFragment() != null -> {
                PullRequestMilestonedEvent.createFromRaw(node.fragments().milestonedEventFragment())
            }
            node.fragments().pullRequestReviewEventFragment() != null -> {
                PullRequestReview.createFromRaw(node.fragments().pullRequestReviewEventFragment())
            }
            node.fragments().pullRequestReviewThreadFragment() != null -> {
                PullRequestReviewThread.createFromRaw(node.fragments().pullRequestReviewThreadFragment())
            }
            node.fragments().referencedEventFragment() != null -> {
                PullRequestReferencedEvent.createFromRaw(node.fragments().referencedEventFragment())
            }
            node.fragments().renamedTitleEventFragment() != null -> {
                PullRequestRenamedTitleEvent.createFromRaw(node.fragments().renamedTitleEventFragment())
            }
            node.fragments().reopenedEventFragment() != null -> {
                PullRequestReopenedEvent.createFromRaw(node.fragments().reopenedEventFragment())
            }
            node.fragments().reviewDismissedEventFragment() != null -> {
                PullRequestReviewDismissedEvent.createFromRaw(node.fragments().reviewDismissedEventFragment())
            }
            node.fragments().reviewRequestRemovedEventFragment() != null -> {
                PullRequestReviewRequestRemovedEvent.createFromRaw(node.fragments().reviewDismissedEventFragment())
            }
            node.fragments().reviewRequestedEventFragment() != null -> {
                PullRequestReviewRequestedEvent.createFromRaw(node.fragments().reviewRequestedEventFragment())
            }
            node.fragments().unassignedEventFragment() != null -> {
                PullRequestUnassignedEvent.createFromRaw(node.fragments().unassignedEventFragment())
            }
            node.fragments().unlabeledEventFragment() != null -> {
                PullRequestUnlabeledEvent.createFromRaw(node.fragments().unlabeledEventFragment())
            }
            node.fragments().unlockedEventFragment() != null -> {
                PullRequestUnlockedEvent.createFromRaw(node.fragments().unlockedEventFragment())
            }
            else -> {
                // unsupported type, just ignore it.
                null
            }
        }
    }

}