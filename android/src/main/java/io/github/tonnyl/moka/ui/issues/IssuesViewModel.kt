package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.data.IssuePullRequestQueryState
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class IssuesViewModel(
    accountInstance: AccountInstance,
    owner: String,
    name: String,
    queryState: IssuePullRequestQueryState
) : ViewModel() {

    val issuesFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                IssuesDataSource(
                    api = accountInstance.repositoryApi,
                    owner = owner,
                    name = name,
                    queryState = queryState
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}