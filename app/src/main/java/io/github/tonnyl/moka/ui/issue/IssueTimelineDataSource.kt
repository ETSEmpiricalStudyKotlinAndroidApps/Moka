package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.IssueTimelineQuery
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.net.Status
import timber.log.Timber

class IssueTimelineDataSource(
        private val owner: String,
        private val name: String,
        private val number: Int
) : PageKeyedDataSource<String, IssueTimelineItem>() {

    val status = MutableLiveData<Status>()

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, IssueTimelineItem>) {
        status.postValue(Status.LOADING)

        Timber.d("loadInitial")

        val issueTimelineQuery = IssueTimelineQuery.builder()
                .owner(owner)
                .name(name)
                .number(number)
                .perPage(params.requestedLoadSize)
                .build()

        val call = NetworkClient.apolloClient
                .query(issueTimelineQuery)

        try {
            // triggered by a refresh, we better execute sync
            val response = Rx2Apollo.from(call).blockingFirst()

            status.postValue(if (response.hasErrors().not()) Status.ERROR else Status.SUCCESS)

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

        } catch (e: Exception) {
            status.postValue(Status.ERROR)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, IssueTimelineItem>) {
        Timber.d("loadAfter")

        val timelineQuery = IssueTimelineQuery.builder()
                .owner(owner)
                .name(name)
                .number(number)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

        NetworkClient.apolloClient
                .query(timelineQuery)
                .enqueue(object : ApolloCall.Callback<IssueTimelineQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e, "loadAfter error: ${e.message}")
                    }

                    override fun onResponse(response: Response<IssueTimelineQuery.Data>) {
                        val list = mutableListOf<IssueTimelineItem>()
                        val timeline = response.data()?.repository()?.issue()?.timeline()

                        timeline?.nodes()?.forEach { node ->
                            list.add(initTimelineItemWithRawData(node))
                        }

                        callback.onResult(list, if (timeline?.pageInfo()?.hasNextPage() == true) timeline.pageInfo().endCursor() else null)
                    }

                })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, IssueTimelineItem>) {
        Timber.d("loadBefore")

        val timelineQuery = IssueTimelineQuery.builder()
                .owner(owner)
                .name(name)
                .number(number)
                .perPage(params.requestedLoadSize)
                .before(params.key)
                .build()

        NetworkClient.apolloClient
                .query(timelineQuery)
                .enqueue(object : ApolloCall.Callback<IssueTimelineQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e, "loadAfter error: ${e.message}")
                    }

                    override fun onResponse(response: Response<IssueTimelineQuery.Data>) {
                        val list = mutableListOf<IssueTimelineItem>()
                        val timeline = response.data()?.repository()?.issue()?.timeline()

                        timeline?.nodes()?.forEach { node ->
                            list.add(initTimelineItemWithRawData(node))
                        }

                        callback.onResult(list, if (timeline?.pageInfo()?.hasPreviousPage() == true) timeline.pageInfo().startCursor() else null)
                    }

                })
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