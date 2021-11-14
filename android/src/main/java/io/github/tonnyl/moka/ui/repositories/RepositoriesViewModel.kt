package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class RepositoriesViewModel(
    accountInstance: AccountInstance,
    login: String,
    repoName: String?,
    repositoryType: RepositoryType
) : ViewModel() {

    val repositoriesFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                RepositoriesDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    login = login,
                    repoName = repoName,
                    repositoryType = repositoryType
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}