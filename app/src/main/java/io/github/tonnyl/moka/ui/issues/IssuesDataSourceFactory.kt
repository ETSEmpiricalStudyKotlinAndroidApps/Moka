package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource

class IssuesDataSourceFactory(
    private val owner: String,
    private val name: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<IssueItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource2<List<IssueItem>>>
) : DataSource.Factory<String, IssueItem>() {

    private var issueDataSource: IssuesDataSource? = null

    override fun create(): DataSource<String, IssueItem> {
        return IssuesDataSource(owner, name, initialLoadStatus, pagedLoadStatus).also {
            issueDataSource = it
        }
    }

    fun retryLoadPreviousNext() {
        issueDataSource?.retry?.invoke()
    }

}