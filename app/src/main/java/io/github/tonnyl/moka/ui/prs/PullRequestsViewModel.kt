package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.item.PullRequestItem

class PullRequestsViewModel(
        private val owner: String,
        private val name: String
) : ViewModel() {

    private val sourceFactory = PullRequestDataSourceFactory(owner, name)

    val issuesResults: LiveData<PagedList<PullRequestItem>> by lazy {
        val config = PagedList.Config.Builder()
                .setPageSize(20)
                .setInitialLoadSizeHint(20 * 1)
                .setEnablePlaceholders(false)
                .build()

        LivePagedListBuilder(sourceFactory, config).build()
    }

}