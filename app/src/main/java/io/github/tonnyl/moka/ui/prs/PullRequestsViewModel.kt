package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.MokaApp

class PullRequestsViewModel(
    owner: String,
    name: String
) : ViewModel() {

    val prsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                PullRequestsDataSource(owner, name)
            }
        ).flow.cachedIn(viewModelScope)
    }

}