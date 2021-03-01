package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.PullRequest

class PullRequestViewModel(
    owner: String,
    name: String,
    number: Int
) : ViewModel() {

    private val _pullRequest = MutableLiveData<PullRequest>()
    val pullRequest: LiveData<PullRequest>
        get() = _pullRequest

    val prTimelineFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                PullRequestTimelineDataSource(
                    owner = owner,
                    name = name,
                    number = number,
                    _pullRequest
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}