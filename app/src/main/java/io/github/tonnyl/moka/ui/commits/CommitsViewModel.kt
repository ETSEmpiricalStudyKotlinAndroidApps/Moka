package io.github.tonnyl.moka.ui.commits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class CommitsViewModel(
    accountInstance: AccountInstance,
    isOrg: Boolean,
    login: String,
    repoName: String
) : ViewModel() {

    val commitsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                CommitsDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    isOrg = isOrg,
                    login = login,
                    repoName = repoName
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}