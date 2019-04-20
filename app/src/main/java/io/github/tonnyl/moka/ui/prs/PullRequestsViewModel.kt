package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.data.item.PullRequestItem

class PullRequestsViewModel(
        private val owner: String,
        private val name: String
) : ViewModel() {

    private val _loadStatusLiveData = MutableLiveData<PagedResource<List<PullRequestItem>>>()
    val loadStatusLiveData: LiveData<PagedResource<List<PullRequestItem>>>
        get() = _loadStatusLiveData

    private val sourceFactory = PullRequestDataSourceFactory(owner, name, _loadStatusLiveData)

    val issuesResults: LiveData<PagedList<PullRequestItem>> by lazy {
        val config = PagedList.Config.Builder()
                .setPageSize(20)
                .setInitialLoadSizeHint(20 * 1)
                .setEnablePlaceholders(false)
                .build()

        LivePagedListBuilder(sourceFactory, config).build()
    }

    fun refresh() {
        sourceFactory.invalidate()
    }

}