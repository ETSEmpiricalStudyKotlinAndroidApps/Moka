package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.item.IssueItem

class IssuesViewModel(
        private val owner: String,
        private val name: String
) : ViewModel() {

    private val sourceFactory = IssuesDataSourceFactory(owner, name)

    val issuesResults: LiveData<PagedList<IssueItem>> by lazy {
        val config = PagedList.Config.Builder()
                .setPageSize(20)
                .setInitialLoadSizeHint(20 * 1)
                .setEnablePlaceholders(false)
                .build()

        LivePagedListBuilder(sourceFactory, config).build()
    }

}