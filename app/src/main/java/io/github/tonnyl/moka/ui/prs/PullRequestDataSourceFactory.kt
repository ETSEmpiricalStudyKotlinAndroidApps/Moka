package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.data.item.PullRequestItem
import kotlinx.coroutines.CoroutineScope

class PullRequestDataSourceFactory(
    private val coroutineScope: CoroutineScope,
    private val owner: String,
    private val name: String,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<PullRequestItem>>>
) : DataSource.Factory<String, PullRequestItem>() {

    private val pullRequestsLiveData = MutableLiveData<PullRequestsDataSource>()

    override fun create(): DataSource<String, PullRequestItem> {
        return PullRequestsDataSource(coroutineScope, owner, name, loadStatusLiveData).apply {
            pullRequestsLiveData.postValue(this)
        }
    }

    fun invalidate() {
        pullRequestsLiveData.value?.invalidate()
    }

}