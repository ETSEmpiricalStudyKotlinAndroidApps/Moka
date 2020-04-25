package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.Issue
import io.github.tonnyl.moka.data.item.IssueTimelineItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource

class IssueTimelineSourceFactory(
    private val owner: String,
    private val name: String,
    private val number: Int,
    private val issueData: MutableLiveData<Issue>,
    private val initialLoadStatus: MutableLiveData<Resource<List<IssueTimelineItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource<List<IssueTimelineItem>>>
) : DataSource.Factory<String, IssueTimelineItem>() {

    private var sourceFactory: IssueTimelineDataSource? = null

    override fun create(): DataSource<String, IssueTimelineItem> {
        return IssueTimelineDataSource(
            owner,
            name,
            number,
            issueData,
            initialLoadStatus,
            pagedLoadStatus
        ).also {
            sourceFactory = it
        }
    }

    fun retryLoadPreviousNext() {
        sourceFactory?.retry?.invoke()
    }

}