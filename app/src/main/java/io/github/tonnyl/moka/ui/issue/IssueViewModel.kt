package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.Issue
import io.github.tonnyl.moka.data.item.IssueTimelineItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel

class IssueViewModel(
    val args: IssueFragmentArgs
) : NetworkCacheSourceViewModel<IssueTimelineItem>() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<IssueTimelineItem>>>()
    val initialLoadStatus: LiveData<Resource<List<IssueTimelineItem>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource<List<IssueTimelineItem>>>()
    val pagedLoadStatus: LiveData<PagedResource<List<IssueTimelineItem>>>
        get() = _pagedLoadStatus

    private val _issueLiveData = MutableLiveData<Issue>()
    val issueLiveData: LiveData<Issue>
        get() = _issueLiveData

    private lateinit var sourceFactory: IssueTimelineSourceFactory

    init {
        refresh()
    }

    override fun initRemoteSource(): LiveData<PagedList<IssueTimelineItem>> {
        sourceFactory = IssueTimelineSourceFactory(
            args.repositoryOwner,
            args.repositoryName,
            args.number,
            _issueLiveData,
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