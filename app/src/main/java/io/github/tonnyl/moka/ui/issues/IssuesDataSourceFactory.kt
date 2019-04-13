package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.PagedResource
import io.github.tonnyl.moka.data.item.IssueItem

class IssuesDataSourceFactory(
        private val owner: String,
        private val name: String,
        private val loadStatusLiveData: MutableLiveData<PagedResource<List<IssueItem>>>
) : DataSource.Factory<String, IssueItem>() {

    private val issuesLiveData = MutableLiveData<IssuesDataSource>()

    override fun create(): DataSource<String, IssueItem> = IssuesDataSource(owner, name, loadStatusLiveData).apply {
        issuesLiveData.postValue(this)
    }

    fun invalidate() {
        issuesLiveData.value?.invalidate()
    }

}