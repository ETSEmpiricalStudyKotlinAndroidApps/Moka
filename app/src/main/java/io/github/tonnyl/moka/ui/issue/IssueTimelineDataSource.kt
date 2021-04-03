package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.tonnyl.moka.data.Issue
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.data.toNonNullIssue
import io.github.tonnyl.moka.network.queries.queryIssue
import io.github.tonnyl.moka.network.queries.queryIssueTimelineItems
import io.github.tonnyl.moka.queries.IssueQuery
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.addedToProjectEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.assignedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.closedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.convertedNoteToIssueEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.crossReferencedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.demilestonedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.issueCommentFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.labeledEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.lockedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.markedAsDuplicateEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.milestonedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.movedColumnsInProjectEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.pinnedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.referencedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.removedFromProjectEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.renamedTitleEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.reopenedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.transferredEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.unassignedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.unlabeledEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.unlockedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.unpinnedEventFragment
import io.github.tonnyl.moka.queries.IssueQuery.Data.Repository.Issue.TimelineItems.PageInfo.Companion.pageInfo
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.addedToProjectEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.assignedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.closedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.convertedNoteToIssueEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.crossReferencedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.demilestonedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.issueCommentFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.labeledEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.lockedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.markedAsDuplicateEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.milestonedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.movedColumnsInProjectEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.pinnedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.referencedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.removedFromProjectEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.renamedTitleEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.reopenedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.transferredEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.unassignedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.unlabeledEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.unlockedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes.Companion.unpinnedEventFragment
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.PageInfo.Companion.pageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class IssueTimelineDataSource(
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
                    val issue = queryIssue(
                        owner = owner,
                        name = name,
                        number = number,
                        perPage = params.loadSize,
                        after = params.key,
                        before = params.key
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
                    val timeline = queryIssueTimelineItems(
                        owner = owner,
                        name = name,
                        number = number,
                        perPage = params.loadSize,
                        after = params.key,
                        before = params.key
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
                Timber.e(e)

                LoadResult.Error(e)
            }
        }
    }

    private fun initTimelineItemWithRawData(node: IssueTimelineItemsQuery.Data.Repository.Issue.TimelineItems.Nodes): IssueTimelineItem? {
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

    private fun initTimelineItemWithRawData(node: IssueQuery.Data.Repository.Issue.TimelineItems.Nodes): IssueTimelineItem? {
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