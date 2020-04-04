package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.item.PullRequestTimelineItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource

class PullRequestTimelineSourceFactory(
    private val owner: String,
    private val name: String,
    private val number: Int,
    private val initialLoadStatus: MutableLiveData<Resource<List<PullRequestTimelineItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource<List<PullRequestTimelineItem>>>
) : DataSource.Factory<String, PullRequestTimelineItem>() {

    private var dataSource: PullRequestTimelineDataSource? = null

    override fun create(): DataSource<String, PullRequestTimelineItem> {
        return PullRequestTimelineDataSource(
            owner,
            name,
            number,
            initialLoadStatus,
            pagedLoadStatus
        ).also {
            dataSource = it
        }
    }

    fun retryLoadPreviousNext() {
        dataSource?.retry?.invoke()
    }

}