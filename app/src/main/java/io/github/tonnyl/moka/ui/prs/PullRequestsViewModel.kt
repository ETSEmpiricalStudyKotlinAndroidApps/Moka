package io.github.tonnyl.moka.ui.prs

import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.liveData
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.prs.PullRequestItemEvent.ViewProfile
import io.github.tonnyl.moka.ui.prs.PullRequestItemEvent.ViewPullRequest

class PullRequestsViewModel(
    private val args: PullRequestsFragmentArgs
) : ViewModel() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<PullRequestItem>>>()
    val initialLoadStatus: LiveData<Resource<List<PullRequestItem>>>
        get() = _initialLoadStatus

    private val _event = MutableLiveData<Event<PullRequestItemEvent>>()
    val event: LiveData<Event<PullRequestItemEvent>>
        get() = _event

    val prsResult = liveData {
        emitSource(
            Pager(
                config = MokaApp.defaultPagingConfig,
                pagingSourceFactory = {
                    PullRequestsDataSource(args.owner, args.name, _initialLoadStatus)
                }
            ).liveData
        )
    }.cachedIn(viewModelScope)

    @MainThread
    fun viewPullRequest(number: Int) {
        _event.value = Event(ViewPullRequest(number))
    }

    @MainThread
    fun viewProfile(login: String) {
        _event.value = Event(ViewProfile(login))
    }

}