package io.github.tonnyl.moka.ui.issues

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel
import io.github.tonnyl.moka.ui.issues.IssueItemEvent.ViewIssueTimeline
import io.github.tonnyl.moka.ui.issues.IssueItemEvent.ViewUserProfile

class IssuesViewModel(
    private val args: IssuesFragmentArgs
) : NetworkCacheSourceViewModel<IssueItem>() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<IssueItem>>>()
    val initialLoadStatus: LiveData<Resource<List<IssueItem>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource<List<IssueItem>>>()
    val pagedLoadStatus: LiveData<PagedResource<List<IssueItem>>>
        get() = _pagedLoadStatus

    private lateinit var sourceFactory: IssuesDataSourceFactory

    private val _event = MutableLiveData<Event<IssueItemEvent>>()
    val event: LiveData<Event<IssueItemEvent>>
        get() = _event

    init {
        refresh()
    }

    override fun initRemoteSource(): LiveData<PagedList<IssueItem>> {
        sourceFactory = IssuesDataSourceFactory(
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
    fun viewUserProfile(login: String) {
        _event.value = Event(ViewUserProfile(login))
    }

    @MainThread
    fun viewIssueTimeline(number: Int) {
        _event.value = Event(ViewIssueTimeline(number))
    }

}