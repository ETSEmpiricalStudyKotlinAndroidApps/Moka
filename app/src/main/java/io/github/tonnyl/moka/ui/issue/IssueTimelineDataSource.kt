package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.Issue
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.data.toNonNullIssue
import io.github.tonnyl.moka.queries.IssueQuery
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.addedToProjectEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.assignedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.closedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.convertedNoteToIssueEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.crossReferencedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.demilestonedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.issueCommentFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.labeledEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.lockedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.markedAsDuplicateEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.milestonedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.movedColumnsInProjectEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.pinnedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.referencedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.removedFromProjectEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.renamedTitleEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.reopenedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.transferredEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.unassignedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.unlabeledEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.unlockedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Node.Companion.unpinnedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.PageInfo.Companion.pageInfo
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.addedToProjectEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.assignedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.closedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.convertedNoteToIssueEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.crossReferencedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.demilestonedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.issueCommentFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.labeledEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.lockedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.markedAsDuplicateEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.milestonedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.movedColumnsInProjectEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.pinnedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.referencedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.removedFromProjectEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.renamedTitleEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.reopenedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.transferredEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.unassignedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.unlabeledEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.unlockedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node.Companion.unpinnedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.PageInfo.Companion.pageInfo
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
                            after = params.key,
                            before = params.key,
                            perPage = params.loadSize
                        )
                    ).data?.repository?.issue

                    issue?.toNonNullIssue()?.let {
                        issueData.postValue(it)
                    }

                    list.addAll(
                        issue?.timelineItems?.nodes.orEmpty().mapNotNull { node ->
                            node?.let {
                                initTimelineItemWithRawData(it)
                            }
                        }
                    )

                    val pageInfo = issue?.timelineItems?.pageInfo?.pageInfo()

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
                                after = params.key,
                                before = params.key,
                                perPage = params.loadSize
                            )
                        ).data?.repository?.issue?.timelineItems

                    list.addAll(
                        timeline?.nodes.orEmpty().mapNotNull { node ->
                            node?.let {
                                initTimelineItemWithRawData(it)
                            }
                        }
                    )

                    val pageInfo = timeline?.pageInfo?.pageInfo()

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

    private fun initTimelineItemWithRawData(node: IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Node): IssueTimelineItem? {
        return node.addedToProjectEventFragment()?.toNonNullAddedToProjectEvent()
            ?: node.assignedEventFragment()?.toNonNullAssignedEvent()
            ?: node.closedEventFragment()?.toNonNullClosedEvent()
            ?: node.convertedNoteToIssueEventFragment()?.toNonNullConvertedNoteToIssueEvent()
            ?: node.crossReferencedEventFragment()?.toNonNullCrossReferencedEvent()
            ?: node.demilestonedEventFragment()?.toNonNullDemilestonedEvent()
            ?: node.issueCommentFragment()?.toNonNullIssueComment(owner, name)
            ?: node.labeledEventFragment()?.toNonNullLabeledEvent()
            ?: node.lockedEventFragment()?.toNonNullLockedEvent()
            ?: node.markedAsDuplicateEventFragment()?.toNonNullMarkedAsDuplicateEvent()
            ?: node.milestonedEventFragment()?.toNonNullMilestonedEvent()
            ?: node.movedColumnsInProjectEventFragment()?.toNonNullMovedColumnsInProjectEvent()
            ?: node.pinnedEventFragment()?.toNonNullPinnedEvent()
            ?: node.referencedEventFragment()?.toNonNullReferencedEvent()
            ?: node.removedFromProjectEventFragment()?.toNonNullRemovedFromProjectEvent()
            ?: node.renamedTitleEventFragment()?.toNonNullRenamedTitleEvent()
            ?: node.reopenedEventFragment()?.toNonNullReopenedEvent()
            ?: node.transferredEventFragment()?.toNonNullTransferredEvent()
            ?: node.unassignedEventFragment()?.toNonNullUnassignedEvent()
            ?: node.unlabeledEventFragment()?.toNonNullUnlabeledEvent()
            ?: node.unlockedEventFragment()?.toNonNullUnlockedEvent()
            ?: node.unpinnedEventFragment()?.toNonNullUnpinnedEvent()
    }

    private fun initTimelineItemWithRawData(node: IssueQuery.Data.Repository.Issue.TimelineItems.Node): IssueTimelineItem? {
        return node.addedToProjectEventFragment()?.toNonNullAddedToProjectEvent()
            ?: node.assignedEventFragment()?.toNonNullAssignedEvent()
            ?: node.closedEventFragment()?.toNonNullClosedEvent()
            ?: node.convertedNoteToIssueEventFragment()?.toNonNullConvertedNoteToIssueEvent()
            ?: node.crossReferencedEventFragment()?.toNonNullCrossReferencedEvent()
            ?: node.demilestonedEventFragment()?.toNonNullDemilestonedEvent()
            ?: node.issueCommentFragment()?.toNonNullIssueComment(owner, name)
            ?: node.labeledEventFragment()?.toNonNullLabeledEvent()
            ?: node.lockedEventFragment()?.toNonNullLockedEvent()
            ?: node.markedAsDuplicateEventFragment()?.toNonNullMarkedAsDuplicateEvent()
            ?: node.milestonedEventFragment()?.toNonNullMilestonedEvent()
            ?: node.movedColumnsInProjectEventFragment()?.toNonNullMovedColumnsInProjectEvent()
            ?: node.pinnedEventFragment()?.toNonNullPinnedEvent()
            ?: node.referencedEventFragment()?.toNonNullReferencedEvent()
            ?: node.removedFromProjectEventFragment()?.toNonNullRemovedFromProjectEvent()
            ?: node.renamedTitleEventFragment()?.toNonNullRenamedTitleEvent()
            ?: node.reopenedEventFragment()?.toNonNullReopenedEvent()
            ?: node.transferredEventFragment()?.toNonNullTransferredEvent()
            ?: node.unassignedEventFragment()?.toNonNullUnassignedEvent()
            ?: node.unlabeledEventFragment()?.toNonNullUnlabeledEvent()
            ?: node.unlockedEventFragment()?.toNonNullUnlockedEvent()
            ?: node.unpinnedEventFragment()?.toNonNullUnpinnedEvent()
    }

    override fun getRefreshKey(state: PagingState<String, IssueTimelineItem>): String? {
        return null
    }

}