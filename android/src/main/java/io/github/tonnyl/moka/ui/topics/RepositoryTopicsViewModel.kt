package io.github.tonnyl.moka.ui.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.ui.defaultPagingConfig
import io.tonnyl.moka.common.AccountInstance

data class RepositoryTopicsViewModelExtra(
    val accountInstance: AccountInstance,
    val login: String,
    val repoName: String
)

class RepositoryTopicsViewModel(extra: RepositoryTopicsViewModelExtra) : ViewModel() {

    val topicsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                RepositoryTopicsDataSource(
                    apolloClient = extra.accountInstance.apolloGraphQLClient.apolloClient,
                    login = extra.login,
                    name = extra.repoName
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}