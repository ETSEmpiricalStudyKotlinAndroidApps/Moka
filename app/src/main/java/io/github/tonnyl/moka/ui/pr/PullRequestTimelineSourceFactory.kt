package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.item.PullRequestTimelineItem

class PullRequestTimelineSourceFactory(
        private val owner: String,
        private val name: String,
        private val number: Int
) : DataSource.Factory<String, PullRequestTimelineItem>() {

    private val pullRequestTimelineLiveData = MutableLiveData<PullRequestTimelineDataSource>()

    override fun create(): DataSource<String, PullRequestTimelineItem> = PullRequestTimelineDataSource(owner, name, number).apply {
        pullRequestTimelineLiveData.postValue(this)
    }

}