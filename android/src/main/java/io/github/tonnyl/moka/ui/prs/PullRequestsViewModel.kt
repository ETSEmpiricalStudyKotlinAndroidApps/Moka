package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.data.IssuePullRequestQueryState
import io.tonnyl.moka.common.ui.defaultPagingConfig

data class PullRequestsViewModelExtra(
    val accountInstance: AccountInstance,
    val owner: String,
    val name: String,
    val state: IssuePullRequestQueryState
)

class PullRequestsViewModel(extra: PullRequestsViewModelExtra) : ViewModel() {

    val prsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                PullRequestsDataSource(
                    api = extra.accountInstance.repositoryApi,
                    owner = extra.owner,
                    name = extra.name,
                    queryState = extra.state
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}