package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.liveData
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.PullRequest
import io.github.tonnyl.moka.data.item.PullRequestTimelineItem
import io.github.tonnyl.moka.network.Resource

class PullRequestViewModel(
    val args: PullRequestFragmentArgs
) : ViewModel() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<PullRequestTimelineItem>>>()
    val initialLoadStatus: LiveData<Resource<List<PullRequestTimelineItem>>>
        get() = _initialLoadStatus

    private val _pullRequest = MutableLiveData<PullRequest>()
    val pullRequest: LiveData<PullRequest>
        get() = _pullRequest

    val prTimelineResult = liveData {
        emitSource(
            Pager(
                config = MokaApp.defaultPagingConfig,
                pagingSourceFactory = {
                    PullRequestTimelineDataSource(
                        args.repositoryOwner,
                        args.repositoryName,
                        args.number,
                        _pullRequest,
                        _initialLoadStatus
                    )
                }
            ).liveData
        )
    }.cachedIn(viewModelScope)

}