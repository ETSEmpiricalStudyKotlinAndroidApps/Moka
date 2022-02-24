package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
data class RepositoriesViewModelExtra(
    val accountInstance: AccountInstance,
    val login: String,
    val repoName: String?,
    val queryOption: RepositoriesQueryOption
)

@ExperimentalSerializationApi
class RepositoriesViewModel(extra: RepositoriesViewModelExtra) : ViewModel() {

    val repositoriesFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                RepositoriesDataSource(
                    apolloClient = extra.accountInstance.apolloGraphQLClient.apolloClient,
                    login = extra.login,
                    repoName = extra.repoName,
                    queryOption = extra.queryOption
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}