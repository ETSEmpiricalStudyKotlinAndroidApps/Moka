package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import io.github.tonnyl.moka.data.Issue
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.data.toNonNullIssue
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryIssue
import io.github.tonnyl.moka.network.queries.queryIssueTimelineItems
import io.github.tonnyl.moka.queries.IssueQuery
import io.github.tonnyl.moka.queries.IssueTimelineItemsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class IssueTimelineDataSource(
    private val owner: String,
    private val name: String,
    private val number: Int,
    private val issueData: MutableLiveData<Issue>,
    private val initialLoadStatus: MutableLiveData<Resource<List<IssueTimelineItem>>>
) : PagingSource<String, IssueTimelineItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, IssueTimelineItem> {
        val list = mutableListOf<IssueTimelineItem>()
        return withContext(Dispatchers.IO) {
            try {
                if (params is LoadParams.Refresh) {
                    initialLoadStatus.postValue(Resource.loading(null))

                    val issue = queryIssue(
                        owner = owner,
                        name = name,
                        number = number,
                        perPage = params.loadSize,
                        after = params.key,
                        before = params.key
                    ).data()?.repository?.issue

                    issue?.toNonNullIssue()?.let {
                        issueData.postValue(it)
                    }

                    issue?.timelineItems?.nodes?.forEach { node ->
                        node?.let {
                            initTimelineItemWithRawData(node)?.let {
                                list.add(it)
                            }
                        }
                    }

                    val pageInfo = issue?.timelineItems?.pageInfo?.fragments?.pageInfo

                    LoadResult.Page(
                        data = list,
                        prevKey = pageInfo.checkedStartCursor,
                        nextKey = pageInfo.checkedEndCursor
                    ).also {
                        initialLoadStatus.postValue(Resource.success(list))
                    }
                } else {
                    val timeline = queryIssueTimelineItems(
                        owner = owner,
                        name = name,
                        number = number,
                        perPage = params.loadSize,
                        after = params.key,
                        before = params.key
                    ).data()?.repository?.issue?.timelineItems

                    timeline?.nodes?.forEach { node ->
                        node?.let {
                            initTimelineItemWithRawData(node)?.let {
                                list.add(it)
                            }
                        }
                    }

                    val pageInfo = timeline?.pageInfo?.fragments?.pageInfo

                    LoadResult.Page(
                        data = list,
                        prevKey = pageInfo.checkedStartCursor,
                        nextKey = pageInfo.checkedEndCursor
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)

                if (params is LoadParams.Refresh) {
                    initialLoadStatus.postValue(Resource.error(e.message, null))
                }

                LoadResult.Error<String, IssueTimelineItem>(e)
            }
        }
    }

    private fun initTimelineItemWithRawData(node: IssueTimelineItemsQuery.Node): IssueTimelineItem? {
        return when {
            node.fragments.addedToProjectEventFragment != null -> {
                node.fragments.addedToProjectEventFragment.toNonNullAddedToProjectEvent()
            }
            node.fragments.assignedEventFragment != null -> {
                node.fragments.assignedEventFragment.toNonNullAssignedEvent()
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
            //     node.fragments.mentionedEventFragment.toNonNullMentionedEvent()
            // }
            node.fragments.milestonedEventFragment != null -> {
                node.fragments.milestonedEventFragment.toNonNullMilestonedEvent()
            }
            node.fragments.movedColumnsInProjectEventFragment != null -> {
                node.fragments.movedColumnsInProjectEventFragment.toNonNullMovedColumnsInProjectEvent()
            }
            node.fragments.pinnedEventFragment != null -> {
                node.fragments.pinnedEventFragment.toNonNullPinnedEvent()
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

    private fun initTimelineItemWithRawData(node: IssueQuery.Node): IssueTimelineItem? {
        return when {
            node.fragments.addedToProjectEventFragment != null -> {
                node.fragments.addedToProjectEventFragment.toNonNullAddedToProjectEvent()
            }
            node.fragments.assignedEventFragment != null -> {
                node.fragments.assignedEventFragment.toNonNullAssignedEvent()
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
            //     node.fragments.mentionedEventFragment.toNonNullMentionedEvent()
            // }
            node.fragments.milestonedEventFragment != null -> {
                node.fragments.milestonedEventFragment.toNonNullMilestonedEvent()
            }
            node.fragments.movedColumnsInProjectEventFragment != null -> {
                node.fragments.movedColumnsInProjectEventFragment.toNonNullMovedColumnsInProjectEvent()
            }
            node.fragments.pinnedEventFragment != null -> {
                node.fragments.pinnedEventFragment.toNonNullPinnedEvent()
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