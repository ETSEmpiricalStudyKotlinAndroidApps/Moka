package io.github.tonnyl.moka.ui.issues

import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.liveData
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.issues.IssueItemEvent.ViewIssueTimeline
import io.github.tonnyl.moka.ui.issues.IssueItemEvent.ViewUserProfile

class IssuesViewModel(
    private val args: IssuesFragmentArgs
) : ViewModel() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<IssueItem>>>()
    val initialLoadStatus: LiveData<Resource<List<IssueItem>>>
        get() = _initialLoadStatus

    private val _event = MutableLiveData<Event<IssueItemEvent>>()
    val event: LiveData<Event<IssueItemEvent>>
        get() = _event

    val issuesResult = liveData {
        emitSource(
            Pager(
                config = MokaApp.defaultPagingConfig,
                pagingSourceFactory = {
                    IssuesDataSource(args.owner, args.name, _initialLoadStatus)
                }
            ).liveData
        )
    }.cachedIn(viewModelScope)

    @MainThread
    fun viewUserProfile(login: String) {
        _event.value = Event(ViewUserProfile(login))
    }

    @MainThread
    fun viewIssueTimeline(number: Int) {
        _event.value = Event(ViewIssueTimeline(number))
    }

}