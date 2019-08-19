package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel

class PullRequestsViewModel(
    private val owner: String,
    private val name: String
) : NetworkCacheSourceViewModel<PullRequestItem>() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<PullRequestItem>>>()
    val initialLoadStatus: LiveData<Resource<List<PullRequestItem>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource2<List<PullRequestItem>>>()
    val pagedLoadStatus: LiveData<PagedResource2<List<PullRequestItem>>>
        get() = _pagedLoadStatus

    private lateinit var sourceFactory: PullRequestDataSourceFactory

    init {
        refresh()
    }

    override fun initRemoteSource(): LiveData<PagedList<PullRequestItem>> {
        sourceFactory = PullRequestDataSourceFactory(
            owner,
            name,
            _initialLoadStatus,
            _pagedLoadStatus
        )

        return LivePagedListBuilder(sourceFactory, pagingConfig)
            .build()
    }

    override fun retryLoadPreviousNext() {
        sourceFactory.retryLoadPreviousNext()
    }

}