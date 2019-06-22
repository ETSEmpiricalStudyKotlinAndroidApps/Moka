package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.data.item.IssueTimelineItem
import kotlinx.coroutines.CoroutineScope

class IssueTimelineSourceFactory(
    private val coroutineScope: CoroutineScope,
    private val owner: String,
    private val name: String,
    private val number: Int,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<IssueTimelineItem>>>
) : DataSource.Factory<String, IssueTimelineItem>() {

    private val issueTimelineLiveData = MutableLiveData<IssueTimelineDataSource>()

    override fun create(): DataSource<String, IssueTimelineItem> {
        return IssueTimelineDataSource(coroutineScope, owner, name, number, loadStatusLiveData).apply {
            issueTimelineLiveData.postValue(this)
        }
    }

    fun invalidate() {
        issueTimelineLiveData.value?.invalidate()
    }

}