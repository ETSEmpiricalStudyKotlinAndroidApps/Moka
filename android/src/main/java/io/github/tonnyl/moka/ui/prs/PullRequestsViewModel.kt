package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp
import io.tonnyl.moka.common.data.IssuePullRequestQueryState
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class PullRequestsViewModel(
    accountInstance: AccountInstance,
    owner: String,
    name: String,
    state: IssuePullRequestQueryState
) : ViewModel() {

    val prsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                PullRequestsDataSource(
                    api = accountInstance.repositoryApi,
                    owner = owner,
                    name = name,
                    queryState = state
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}