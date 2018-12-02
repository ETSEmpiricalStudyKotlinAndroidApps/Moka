package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.IssueGraphQL
import io.github.tonnyl.moka.data.item.IssueTimelineItem

class IssueViewModel(
        private val owner: String,
        private val name: String,
        private val number: Int
) : ViewModel() {

    private val sourceFactory = IssueTimelineSourceFactory(owner, name, number)

    val issueTimelineResults: LiveData<PagedList<IssueTimelineItem>> by lazy {
        val config = PagedList.Config.Builder()
                .setPageSize(20)
                .setInitialLoadSizeHint(20 * 1)
                .setEnablePlaceholders(false)
                .build()

        LivePagedListBuilder(sourceFactory, config).build()
    }

    val issueLiveData: LiveData<IssueGraphQL> = Transformations.map(IssueLiveData(owner, name, number)) { it }

}