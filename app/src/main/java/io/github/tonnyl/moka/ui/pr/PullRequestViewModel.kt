package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.PullRequest
import io.github.tonnyl.moka.data.item.PullRequestTimelineItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel

class PullRequestViewModel(
    val args: PullRequestFragmentArgs
) : NetworkCacheSourceViewModel<PullRequestTimelineItem>() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<PullRequestTimelineItem>>>()
    val initialLoadStatus: LiveData<Resource<List<PullRequestTimelineItem>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource<List<PullRequestTimelineItem>>>()
    val pagedLoadStatus: LiveData<PagedResource<List<PullRequestTimelineItem>>>
        get() = _pagedLoadStatus

    private val _pullRequest = MutableLiveData<PullRequest>()
    val pullRequest: LiveData<PullRequest>
        get() = _pullRequest

    private lateinit var sourceFactory: PullRequestTimelineSourceFactory

    init {
        refresh()
    }

    override fun initRemoteSource(): LiveData<PagedList<PullRequestTimelineItem>> {
        sourceFactory = PullRequestTimelineSourceFactory(
            args.repositoryOwner,
            args.repositoryName,
            args.number,
            _pullRequest,
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