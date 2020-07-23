package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.liveData
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.Issue
import io.github.tonnyl.moka.data.item.IssueTimelineItem
import io.github.tonnyl.moka.network.Resource

class IssueViewModel(
    val args: IssueFragmentArgs
) : ViewModel() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<IssueTimelineItem>>>()
    val initialLoadStatus: LiveData<Resource<List<IssueTimelineItem>>>
        get() = _initialLoadStatus

    private val _issueLiveData = MutableLiveData<Issue>()
    val issueLiveData: LiveData<Issue>
        get() = _issueLiveData

    val issueTimelineResult = liveData {
        emitSource(
            Pager(
                config = MokaApp.defaultPagingConfig,
                pagingSourceFactory = {
                    IssueTimelineDataSource(
                        args.repositoryOwner,
                        args.repositoryName,
                        args.number,
                        _issueLiveData,
                        _initialLoadStatus
                    )
                }
            ).liveData
        )
    }.cachedIn(viewModelScope)

}