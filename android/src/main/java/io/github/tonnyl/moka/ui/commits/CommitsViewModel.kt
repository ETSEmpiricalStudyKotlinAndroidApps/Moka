package io.github.tonnyl.moka.ui.commits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class CommitsViewModel(
    accountInstance: AccountInstance,
    login: String,
    repoName: String,
    qualifiedName: String
) : ViewModel() {

    val commitsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                CommitsDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    login = login,
                    repoName = repoName,
                    qualifiedName = qualifiedName
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}