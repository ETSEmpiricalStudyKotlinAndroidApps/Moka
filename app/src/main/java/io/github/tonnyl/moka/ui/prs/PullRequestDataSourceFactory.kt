package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.PagedResource
import io.github.tonnyl.moka.data.item.PullRequestItem

class PullRequestDataSourceFactory(
        private val owner: String,
        private val name: String,
        private val loadStatusLiveData: MutableLiveData<PagedResource<List<PullRequestItem>>>
) : DataSource.Factory<String, PullRequestItem>() {

    private val pullRequestsLiveData = MutableLiveData<PullRequestsDataSource>()

    override fun create(): DataSource<String, PullRequestItem> = PullRequestsDataSource(owner, name, loadStatusLiveData).apply {
        pullRequestsLiveData.postValue(this)
    }

    fun invalidate() {
        pullRequestsLiveData.value?.invalidate()
    }

}