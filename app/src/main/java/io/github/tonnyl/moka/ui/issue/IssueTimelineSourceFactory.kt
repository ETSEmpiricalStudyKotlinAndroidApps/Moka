package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.item.IssueTimelineItem

class IssueTimelineSourceFactory(
        private val owner: String,
        private val name: String,
        private val number: Int
) : DataSource.Factory<String, IssueTimelineItem>() {

    private val issueTimelineLiveData = MutableLiveData<IssueTimelineDataSource>()

    override fun create(): DataSource<String, IssueTimelineItem> = IssueTimelineDataSource(owner, name, number).apply {
        issueTimelineLiveData.postValue(this)
    }

}