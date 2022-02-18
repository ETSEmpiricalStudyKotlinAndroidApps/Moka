package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.benasher44.uuid.Uuid
import io.tonnyl.moka.common.data.IssueTimelineItem
import io.tonnyl.moka.common.data.extension.checkedEndCursor
import io.tonnyl.moka.common.data.extension.checkedStartCursor
import io.tonnyl.moka.graphql.IssueQuery
import io.tonnyl.moka.graphql.IssueQuery.Issue
import io.tonnyl.moka.graphql.IssueTimelineItemsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class IssueTimelineDataSource(
    private val apolloClient: ApolloClient,
    private val owner: String,
    private val name: String,
    private val number: Int,
    private val issueData: MutableLiveData<Issue>
) : PagingSource<String, IssueTimelineItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, IssueTimelineItem> {
        val list = mutableListOf<IssueTimelineItem>()
        return withContext(Dispatchers.IO) {
            try {
                if (params is LoadParams.Refresh) {
                    val issue = apolloClient.query(
                        query = IssueQuery(
                            owner = owner,
                            name = name,
                            number = number,
                            after = Optional.presentIfNotNull(params.key),
                            before = Optional.presentIfNotNull(params.key),
                            perPage = params.loadSize
                        )
                    ).execute().data?.repository?.issue

                    issue?.let {
                        issueData.postValue(it)
                    }

                    list.addAll(
                        issue?.timelineItems?.nodes.orEmpty().mapNotNull { node ->
                            node?.let {
                                initTimelineItemWithRawData(it)
                            }
                        }
                    )

                    val pageInfo = issue?.timelineItems?.pageInfo?.pageInfo

                    LoadResult.Page(
                        data = list,
                        prevKey = pageInfo.checkedStartCursor,
                        nextKey = pageInfo.checkedEndCursor
                    )
                } else {
                    val timeline = apolloClient
                        .query(
                            query = IssueTimelineItemsQuery(
                                owner = owner,
                                name = name,
                                number = number,
                                after = Optional.presentIfNotNull(params.key),
                                before = Optional.presentIfNotNull(params.key),
                                perPage = params.loadSize
                            )
                        ).execute().data?.repository?.issue?.timelineItems

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

    private fun initTimelineItemWithRawData(node: IssueTimelineItemsQuery.Node): IssueTimelineItem? {
        val item = IssueTimelineItem(
            addedToProjectEvent = node.addedToProjectEventFragment,
            assignedEvent = node.assignedEventFragment,
            closedEvent = node.closedEventFragment,
            convertedNoteToIssueEvent = node.convertedNoteToIssueEventFragment,
            crossReferencedEvent = node.crossReferencedEventFragment,
            demilestonedEvent = node.demilestonedEventFragment,
            issueComment = node.issueCommentFragment,
            labeledEvent = node.labeledEventFragment,
            lockedEvent = node.lockedEventFragment,
            markedAsDuplicateEvent = node.markedAsDuplicateEventFragment,
            milestonedEvent = node.milestonedEventFragment,
            movedColumnsInProjectEvent = node.movedColumnsInProjectEventFragment,
            pinnedEvent = node.pinnedEventFragment,
            referencedEvent = node.referencedEventFragment,
            removedFromProjectEvent = node.removedFromProjectEventFragment,
            renamedTitleEvent = node.renamedTitleEventFragment,
            reopenedEvent = node.reopenedEventFragment,
            transferredEvent = node.transferredEventFragment,
            unassignedEvent = node.unassignedEventFragment,
            unlabeledEvent = node.unlabeledEventFragment,
            unlockedEvent = node.unlockedEventFragment,
            unpinnedEvent = node.unpinnedEventFragment
        )

        if (item.hashCode() == 0) {
            return null
        }

        return item.apply {
            id = Uuid.randomUUID().toString()
        }
    }

    private fun initTimelineItemWithRawData(node: IssueQuery.Node): IssueTimelineItem? {
        val item = IssueTimelineItem(
            addedToProjectEvent = node.addedToProjectEventFragment,
            assignedEvent = node.assignedEventFragment,
            closedEvent = node.closedEventFragment,
            convertedNoteToIssueEvent = node.convertedNoteToIssueEventFragment,
            crossReferencedEvent = node.crossReferencedEventFragment,
            demilestonedEvent = node.demilestonedEventFragment,
            issueComment = node.issueCommentFragment,
            labeledEvent = node.labeledEventFragment,
            lockedEvent = node.lockedEventFragment,
            markedAsDuplicateEvent = node.markedAsDuplicateEventFragment,
            milestonedEvent = node.milestonedEventFragment,
            movedColumnsInProjectEvent = node.movedColumnsInProjectEventFragment,
            pinnedEvent = node.pinnedEventFragment,
            referencedEvent = node.referencedEventFragment,
            removedFromProjectEvent = node.removedFromProjectEventFragment,
            renamedTitleEvent = node.renamedTitleEventFragment,
            reopenedEvent = node.reopenedEventFragment,
            transferredEvent = node.transferredEventFragment,
            unassignedEvent = node.unassignedEventFragment,
            unlabeledEvent = node.unlabeledEventFragment,
            unlockedEvent = node.unlockedEventFragment,
            unpinnedEvent = node.unpinnedEventFragment
        )

        if (item.hashCode() == 0) {
            return null
        }

        return item.apply {
            id = Uuid.randomUUID().toString()
        }
    }

    override fun getRefreshKey(state: PagingState<String, IssueTimelineItem>): String? {
        return null
    }

}