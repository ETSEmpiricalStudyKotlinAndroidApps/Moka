package io.github.tonnyl.moka.ui.prs

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel
import io.github.tonnyl.moka.ui.prs.PullRequestItemEvent.ViewProfile
import io.github.tonnyl.moka.ui.prs.PullRequestItemEvent.ViewPullRequest

class PullRequestsViewModel(
    private val args: PullRequestsFragmentArgs
) : NetworkCacheSourceViewModel<PullRequestItem>() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<PullRequestItem>>>()
    val initialLoadStatus: LiveData<Resource<List<PullRequestItem>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource2<List<PullRequestItem>>>()
    val pagedLoadStatus: LiveData<PagedResource2<List<PullRequestItem>>>
        get() = _pagedLoadStatus

    private val _event = MutableLiveData<Event<PullRequestItemEvent>>()
    val event: LiveData<Event<PullRequestItemEvent>>
        get() = _event

    private lateinit var sourceFactory: PullRequestDataSourceFactory

    init {
        refresh()
    }

    override fun initRemoteSource(): LiveData<PagedList<PullRequestItem>> {
        sourceFactory = PullRequestDataSourceFactory(
            args.owner,
            args.name,
            _initialLoadStatus,
            _pagedLoadStatus
        )

        return LivePagedListBuilder(sourceFactory, pagingConfig)
            .build()
    }

    override fun retryLoadPreviousNext() {
        sourceFactory.retryLoadPreviousNext()
    }

    @MainThread
    fun viewPullRequest(number: Int) {
        _event.value = Event(ViewPullRequest(number))
    }

    @MainThread
    fun viewProfile(login: String) {
        _event.value = Event(ViewProfile(login))
    }

}