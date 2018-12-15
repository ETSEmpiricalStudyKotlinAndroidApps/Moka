package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.PullRequestGraphQL
import io.github.tonnyl.moka.data.item.PullRequestTimelineItem

class PullRequestViewModel(
        private val owner: String,
        private val name: String,
        private val number: Int
) : ViewModel() {

    private val sourceFactory = PullRequestTimelineSourceFactory(owner, name, number)

    val pullRequestTimelineResults: LiveData<PagedList<PullRequestTimelineItem>> by lazy {
        val config = PagedList.Config.Builder()
                .setPageSize(20)
                .setInitialLoadSizeHint(20 * 1)
                .setEnablePlaceholders(false)
                .build()

        LivePagedListBuilder(sourceFactory, config).build()
    }

    val pullRequestLiveData: LiveData<PullRequestGraphQL> = Transformations.map(PullRequestLiveData(owner, name, number)) { it }

}