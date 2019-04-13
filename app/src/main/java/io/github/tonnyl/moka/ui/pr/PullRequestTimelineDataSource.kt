package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.PullRequestTimelineQuery
import io.github.tonnyl.moka.data.PagedResource
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.net.Resource
import timber.log.Timber

class PullRequestTimelineDataSource(
        private val owner: String,
        private val name: String,
        private val number: Int,
        private val loadStatusLiveData: MutableLiveData<PagedResource<List<PullRequestTimelineItem>>>
) : PageKeyedDataSource<String, PullRequestTimelineItem>() {

    var retry: (() -> Any)? = null

    private var apolloCall: ApolloQueryCall<PullRequestTimelineQuery.Data>? = null

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, PullRequestTimelineItem>) {
        Timber.d("loadInitial")

        loadStatusLiveData.postValue(PagedResource(initial = Resource.loading(null)))

        val pullRequestTimelineQuery = PullRequestTimelineQuery.builder()
                .owner(owner)
                .name(name)
                .number(number)
                .perPage(params.requestedLoadSize)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(pullRequestTimelineQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<PullRequestTimelineQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadInitial(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(initial = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<PullRequestTimelineQuery.Data>) {
                val list = mutableListOf<PullRequestTimelineItem>()
                val timeline = response.data()?.repository()?.pullRequest()?.timeline()

                timeline?.nodes()?.forEach { node ->
                    list.add(initTimelineItemWithRawData(node))
                }

                val pageInfo = timeline?.pageInfo()
                callback.onResult(
                        list,
                        if (pageInfo?.hasPreviousPage() == true) timeline.pageInfo().startCursor() else null,
                        if (pageInfo?.hasNextPage() == true) timeline.pageInfo().endCursor() else null
                )

                retry = null

                loadStatusLiveData.postValue(PagedResource(initial = Resource.success(list)))
            }

        })
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, PullRequestTimelineItem>) {
        Timber.d("loadAfter")

        loadStatusLiveData.postValue(PagedResource(after = Resource.loading(null)))

        val timelineQuery = PullRequestTimelineQuery.builder()
                .owner(owner)
                .name(name)
                .number(number)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(timelineQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<PullRequestTimelineQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(after = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<PullRequestTimelineQuery.Data>) {
                val list = mutableListOf<PullRequestTimelineItem>()
                val timeline = response.data()?.repository()?.pullRequest()?.timeline()

                timeline?.nodes()?.forEach { node ->
                    list.add(initTimelineItemWithRawData(node))
                }

                callback.onResult(list, if (timeline?.pageInfo()?.hasNextPage() == true) timeline.pageInfo().endCursor() else null)

                retry = null

                loadStatusLiveData.postValue(PagedResource(after = Resource.success(list)))
            }

        })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, PullRequestTimelineItem>) {
        Timber.d("loadBefore")

        loadStatusLiveData.postValue(PagedResource(before = Resource.loading(null)))

        val timelineQuery = PullRequestTimelineQuery.builder()
                .owner(owner)
                .name(name)
                .number(number)
                .perPage(params.requestedLoadSize)
                .before(params.key)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(timelineQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<PullRequestTimelineQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadBefore(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(before = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<PullRequestTimelineQuery.Data>) {
                val list = mutableListOf<PullRequestTimelineItem>()
                val timeline = response.data()?.repository()?.pullRequest()?.timeline()

                timeline?.nodes()?.forEach { node ->
                    list.add(initTimelineItemWithRawData(node))
                }

                callback.onResult(list, if (timeline?.pageInfo()?.hasPreviousPage() == true) timeline.pageInfo().startCursor() else null)

                retry = null

                loadStatusLiveData.postValue(PagedResource(before = Resource.success(list)))
            }

        })
    }

    override fun invalidate() {
        super.invalidate()

        if (apolloCall?.isCanceled == false) {
            apolloCall?.cancel()
        }
    }

    private fun initTimelineItemWithRawData(node: PullRequestTimelineQuery.Node): PullRequestTimelineItem = when {
        node.fragments().assignedEventFragment() != null -> {
            PullRequestAssignedEvent.createFromRaw(node.fragments().assignedEventFragment())!!
        }
        node.fragments().baseRefForcePushedEventFragment() != null -> {
            PullRequestBaseRefForcePushedEvent.createFromRaw(node.fragments().baseRefForcePushedEventFragment())!!
        }
        node.fragments().closedEventFragment() != null -> {
            PullRequestClosedEvent.createFromRaw(node.fragments().closedEventFragment())!!
        }
        node.fragments().commitEventFragment() != null -> {
            PullRequestCommitEvent.createFromRaw(node.fragments().commitEventFragment())!!
        }
        node.fragments().crossReferencedEventFragment() != null -> {
            PullRequestCrossReferencedEvent.createFromRaw(node.fragments().crossReferencedEventFragment())!!
        }
        node.fragments().demilestonedEventFragment() != null -> {
            PullRequestDemilestonedEvent.createFromRaw(node.fragments().demilestonedEventFragment())!!
        }
        node.fragments().deployedEventFragment() != null -> {
            PullRequestDeployedEvent.createFromRaw(node.fragments().deployedEventFragment())!!
        }
        node.fragments().deploymentEnvironmentChangedEventFragment() != null -> {
            PullRequestDeploymentEnvironmentChangedEvent.createFromRaw(node.fragments().deploymentEnvironmentChangedEventFragment())!!
        }
        node.fragments().headRefDeletedEventFragment() != null -> {
            PullRequestHeadRefDeletedEvent.createFromRaw(node.fragments().headRefDeletedEventFragment())!!
        }
        node.fragments().headRefForcePushedEventFragment() != null -> {
            PullRequestHeadRefForcePushedEvent.createFromRaw(node.fragments().headRefForcePushedEventFragment())!!
        }
        node.fragments().headRefRestoredEventFragment() != null -> {
            PullRequestHeadRefRestoredEvent.createFromRaw(node.fragments().headRefRestoredEventFragment())!!
        }
        node.fragments().issueCommentFragment() != null -> {
            PullRequestIssueComment.createFromRaw(node.fragments().issueCommentFragment())!!
        }
        node.fragments().labeledEventFragment() != null -> {
            PullRequestLabeledEvent.createFromRaw(node.fragments().labeledEventFragment())!!
        }
        node.fragments().lockedEventFragment() != null -> {
            PullRequestLockedEvent.createFromRaw(node.fragments().lockedEventFragment())!!
        }
        node.fragments().mergedEventFragment() != null -> {
            PullRequestMergedEvent.createFromRaw(node.fragments().mergedEventFragment())!!
        }
        node.fragments().milestonedEventFragment() != null -> {
            PullRequestMilestonedEvent.createFromRaw(node.fragments().milestonedEventFragment())!!
        }
        node.fragments().pullRequestReviewEventFragment() != null -> {
            PullRequestReview.createFromRaw(node.fragments().pullRequestReviewEventFragment())!!
        }
        node.fragments().pullRequestReviewThreadFragment() != null -> {
            PullRequestReviewThread.createFromRaw(node.fragments().pullRequestReviewThreadFragment())!!
        }
        node.fragments().referencedEventFragment() != null -> {
            PullRequestReferencedEvent.createFromRaw(node.fragments().referencedEventFragment())!!
        }
        node.fragments().renamedTitleEventFragment() != null -> {
            PullRequestRenamedTitleEvent.createFromRaw(node.fragments().renamedTitleEventFragment())!!
        }
        node.fragments().reopenedEventFragment() != null -> {
            PullRequestReopenedEvent.createFromRaw(node.fragments().reopenedEventFragment())!!
        }
        node.fragments().reviewDismissedEventFragment() != null -> {
            PullRequestReviewDismissedEvent.createFromRaw(node.fragments().reviewDismissedEventFragment())!!
        }
        node.fragments().reviewRequestRemovedEventFragment() != null -> {
            PullRequestReviewRequestRemovedEvent.createFromRaw(node.fragments().reviewDismissedEventFragment())!!
        }
        node.fragments().reviewRequestedEventFragment() != null -> {
            PullRequestReviewRequestedEvent.createFromRaw(node.fragments().reviewRequestedEventFragment())!!
        }
        node.fragments().unassignedEventFragment() != null -> {
            PullRequestUnassignedEvent.createFromRaw(node.fragments().unassignedEventFragment())!!
        }
        node.fragments().unlabeledEventFragment() != null -> {
            PullRequestUnlabeledEvent.createFromRaw(node.fragments().unlabeledEventFragment())!!
        }
        node.fragments().unlockedEventFragment() != null -> {
            PullRequestUnlockedEvent.createFromRaw(node.fragments().unlockedEventFragment())!!
        }
        else -> {
            PullRequestUnlockedEvent.createFromRaw(node.fragments().unlockedEventFragment())!!
        }
    }

}