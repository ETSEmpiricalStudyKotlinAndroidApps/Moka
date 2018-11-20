package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.item.IssueItem

class IssuesDataSourceFactory(
        private val owner: String,
        private val name: String
) : DataSource.Factory<String, IssueItem>() {

    private val issuesLiveData = MutableLiveData<IssuesDataSource>()

    override fun create(): DataSource<String, IssueItem> = IssuesDataSource(owner, name).apply {
        issuesLiveData.postValue(this)
    }

}