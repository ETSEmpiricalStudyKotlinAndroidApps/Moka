package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.IssueTimelineQuery
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.network.NetworkClient
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class IssueTimelineDataSource(
    private val owner: String,
    private val name: String,
    private val number: Int,
    private val initialLoadStatus: MutableLiveData<Resource<List<IssueTimelineItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource2<List<IssueTimelineItem>>>
) : PageKeyedDataSource<String, IssueTimelineItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, IssueTimelineItem>
    ) {
        Timber.d("loadInitial")

        initialLoadStatus.postValue(Resource.loading(null))

        try {
            val issueTimelineQuery = IssueTimelineQuery.builder()
                .owner(owner)
                .name(name)
                .number(number)
                .perPage(params.requestedLoadSize)
                .build()

            val response = runBlocking {
                NetworkClient.apolloClient
                    .query(issueTimelineQuery)
                    .execute()
            }

            val list = mutableListOf<IssueTimelineItem>()
            val timeline = response.data()?.repository()?.issue()?.timelineItems()

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
        callback: LoadCallback<String, IssueTimelineItem>
    ) {
        Timber.d("loadAfter")

        pagedLoadStatus.postValue(
            PagedResource2(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val timelineQuery = IssueTimelineQuery.builder()
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

            val list = mutableListOf<IssueTimelineItem>()
            val timeline = response.data()?.repository()?.issue()?.timelineItems()

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
        callback: LoadCallback<String, IssueTimelineItem>
    ) {
        Timber.d("loadBefore")

        pagedLoadStatus.postValue(
            PagedResource2(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val timelineQuery = IssueTimelineQuery.builder()
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

            val list = mutableListOf<IssueTimelineItem>()
            val timeline = response.data()?.repository()?.issue()?.timelineItems()

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

    private fun initTimelineItemWithRawData(node: IssueTimelineQuery.Node): IssueTimelineItem? {
        return when {
            node.fragments().assignedEventFragment() != null -> {
                AssignedEvent.createFromRaw(node.fragments().assignedEventFragment())
            }
            node.fragments().closedEventFragment() != null -> {
                ClosedEvent.createFromRaw(node.fragments().closedEventFragment())
            }
            node.fragments().commitEventFragment() != null -> {
                CommitEvent.createFromRaw(node.fragments().commitEventFragment())
            }
            node.fragments().crossReferencedEventFragment() != null -> {
                CrossReferencedEvent.createFromRaw(node.fragments().crossReferencedEventFragment())
            }
            node.fragments().demilestonedEventFragment() != null -> {
                DemilestonedEvent.createFromRaw(node.fragments().demilestonedEventFragment())
            }
            node.fragments().issueCommentFragment() != null -> {
                IssueCommentEvent.createFromRaw(node.fragments().issueCommentFragment())
            }
            node.fragments().labeledEventFragment() != null -> {
                LabeledEvent.createFromRaw(node.fragments().labeledEventFragment())
            }
            node.fragments().lockedEventFragment() != null -> {
                LockedEvent.createFromRaw(node.fragments().lockedEventFragment())
            }
            node.fragments().milestonedEventFragment() != null -> {
                MilestonedEvent.createFromRaw(node.fragments().milestonedEventFragment())
            }
            node.fragments().referencedEventFragment() != null -> {
                ReferencedEvent.createFromRaw(node.fragments().referencedEventFragment())
            }
            node.fragments().renamedTitleEventFragment() != null -> {
                RenamedTitleEvent.createFromRaw(node.fragments().renamedTitleEventFragment())
            }
            node.fragments().reopenedEventFragment() != null -> {
                ReopenedEvent.createFromRaw(node.fragments().reopenedEventFragment())
            }
            node.fragments().transferredEventFragment() != null -> {
                TransferredEvent.createFromRaw(node.fragments().transferredEventFragment())
            }
            node.fragments().unassignedEventFragment() != null -> {
                UnassignedEvent.createFromRaw(node.fragments().unassignedEventFragment())
            }
            node.fragments().unlabeledEventFragment() != null -> {
                UnlabeledEvent.createFromRaw(node.fragments().unlabeledEventFragment())
            }
            node.fragments().unlockedEventFragment() != null -> {
                UnlockedEvent.createFromRaw(node.fragments().unlockedEventFragment())
            }
            else -> {
                null
            }
        }
    }

}