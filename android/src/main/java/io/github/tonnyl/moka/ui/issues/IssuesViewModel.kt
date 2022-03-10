package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.ui.defaultPagingConfig
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.data.IssuePullRequestQueryState

data class IssuesViewModelExtra(
    val accountInstance: AccountInstance,
    val owner: String,
    val name: String,
    val repoId: String,
    val queryState: IssuePullRequestQueryState
)

class IssuesViewModel(extra: IssuesViewModelExtra) : ViewModel() {

    val issuesFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                IssuesDataSource(
                    api = extra.accountInstance.repositoryApi,
                    owner = extra.owner,
                    name = extra.name,
                    queryState = extra.queryState
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}