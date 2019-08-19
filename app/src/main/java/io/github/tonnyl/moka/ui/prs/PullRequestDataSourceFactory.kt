package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource

class PullRequestDataSourceFactory(
    private val owner: String,
    private val name: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<PullRequestItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource2<List<PullRequestItem>>>
) : DataSource.Factory<String, PullRequestItem>() {

    private var dataSource: PullRequestsDataSource? = null

    override fun create(): DataSource<String, PullRequestItem> {
        return PullRequestsDataSource(owner, name, initialLoadStatus, pagedLoadStatus).also {
            dataSource = it
        }
    }

    fun retryLoadPreviousNext() {
        dataSource?.retry?.invoke()
    }

}