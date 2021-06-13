package io.github.tonnyl.moka.ui.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class RepositoryTopicsViewModel(
    accountInstance: AccountInstance,
    isOrg: Boolean,
    login: String,
    repoName: String
) : ViewModel() {

    val topicsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                RepositoryTopicsDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    isOrg = isOrg,
                    login = login,
                    name = repoName
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}