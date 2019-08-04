package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.coroutines.toDeferred
import io.github.tonnyl.moka.IssueTimelineQuery
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.network.NetworkClient
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class IssueTimelineDataSource(
    private val coroutineScope: CoroutineScope,
    private val owner: String,
    private val name: String,
    private val number: Int,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<IssueTimelineItem>>>
) : PageKeyedDataSource<String, IssueTimelineItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, IssueTimelineItem>
    ) {
        Timber.d("loadInitial")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(initial = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val issueTimelineQuery = IssueTimelineQuery.builder()
                        .owner(owner)
                        .name(name)
                        .number(number)
                        .perPage(params.requestedLoadSize)
                        .build()

                    NetworkClient.apolloClient.query(issueTimelineQuery).toDeferred()
                }.await()

                val list = mutableListOf<IssueTimelineItem>()
                val timeline = response.data()?.repository()?.issue()?.timeline()

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

                loadStatusLiveData.value = PagedResource(initial = Resource.success(list))
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadInitial(params, callback)
                }

                loadStatusLiveData.value = PagedResource(initial = Resource.error(e.message, null))
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, IssueTimelineItem>) {
        Timber.d("loadAfter")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(after = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val timelineQuery = IssueTimelineQuery.builder()
                        .owner(owner)
                        .name(name)
                        .number(number)
                        .perPage(params.requestedLoadSize)
                        .after(params.key)
                        .build()

                    NetworkClient.apolloClient.query(timelineQuery).toDeferred()
                }.await()

                val list = mutableListOf<IssueTimelineItem>()
                val timeline = response.data()?.repository()?.issue()?.timeline()

                timeline?.nodes()?.forEach { node ->
                    list.add(initTimelineItemWithRawData(node))
                }

                callback.onResult(
                    list,
                    if (timeline?.pageInfo()?.hasNextPage() == true) timeline.pageInfo().endCursor() else null
                )

                retry = null

                loadStatusLiveData.value = PagedResource(after = Resource.success(list))
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.value = PagedResource(after = Resource.error(e.message, null))
            }
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, IssueTimelineItem>) {
        Timber.d("loadBefore")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(before = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val timelineQuery = IssueTimelineQuery.builder()
                        .owner(owner)
                        .name(name)
                        .number(number)
                        .perPage(params.requestedLoadSize)
                        .before(params.key)
                        .build()

                    NetworkClient.apolloClient.query(timelineQuery).toDeferred()
                }.await()

                val list = mutableListOf<IssueTimelineItem>()
                val timeline = response.data()?.repository()?.issue()?.timeline()

                timeline?.nodes()?.forEach { node ->
                    list.add(initTimelineItemWithRawData(node))
                }

                callback.onResult(
                    list,
                    if (timeline?.pageInfo()?.hasPreviousPage() == true) timeline.pageInfo().startCursor() else null
                )

                retry = null

                loadStatusLiveData.value = PagedResource(before = Resource.success(list))
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadBefore(params, callback)
                }

                loadStatusLiveData.value = PagedResource(before = Resource.error(e.message, null))
            }
        }
    }

    private fun initTimelineItemWithRawData(node: IssueTimelineQuery.Node): IssueTimelineItem {
        return when {
            node.fragments().assignedEventFragment() != null -> {
                AssignedEvent.createFromRaw(node.fragments().assignedEventFragment())!!
            }
            node.fragments().closedEventFragment() != null -> {
                ClosedEvent.createFromRaw(node.fragments().closedEventFragment())!!
            }
            node.fragments().commitEventFragment() != null -> {
                CommitEvent.createFromRaw(node.fragments().commitEventFragment())!!
            }
            node.fragments().crossReferencedEventFragment() != null -> {
                CrossReferencedEvent.createFromRaw(node.fragments().crossReferencedEventFragment())!!
            }
            node.fragments().demilestonedEventFragment() != null -> {
                DemilestonedEvent.createFromRaw(node.fragments().demilestonedEventFragment())!!
            }
            node.fragments().issueCommentFragment() != null -> {
                IssueCommentEvent.createFromRaw(node.fragments().issueCommentFragment())!!
            }
            node.fragments().labeledEventFragment() != null -> {
                LabeledEvent.createFromRaw(node.fragments().labeledEventFragment())!!
            }
            node.fragments().lockedEventFragment() != null -> {
                LockedEvent.createFromRaw(node.fragments().lockedEventFragment())!!
            }
            node.fragments().milestonedEventFragment() != null -> {
                MilestonedEvent.createFromRaw(node.fragments().milestonedEventFragment())!!
            }
            node.fragments().referencedEventFragment() != null -> {
                ReferencedEvent.createFromRaw(node.fragments().referencedEventFragment())!!
            }
            node.fragments().renamedTitleEventFragment() != null -> {
                RenamedTitleEvent.createFromRaw(node.fragments().renamedTitleEventFragment())!!
            }
            node.fragments().reopenedEventFragment() != null -> {
                ReopenedEvent.createFromRaw(node.fragments().reopenedEventFragment())!!
            }
            node.fragments().transferredEventFragment() != null -> {
                TransferredEvent.createFromRaw(node.fragments().transferredEventFragment())!!
            }
            node.fragments().unassignedEventFragment() != null -> {
                UnassignedEvent.createFromRaw(node.fragments().unassignedEventFragment())!!
            }
            node.fragments().unlabeledEventFragment() != null -> {
                UnlabeledEvent.createFromRaw(node.fragments().unlabeledEventFragment())!!
            }
            node.fragments().unlockedEventFragment() != null -> {
                UnlockedEvent.createFromRaw(node.fragments().unlockedEventFragment())!!
            }
            else -> {
                UnlockedEvent.createFromRaw(node.fragments().unlockedEventFragment())!!
            }
        }
    }

}