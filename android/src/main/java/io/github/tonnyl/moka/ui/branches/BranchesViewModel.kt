package io.github.tonnyl.moka.ui.branches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp
import io.tonnyl.moka.graphql.type.RefOrder
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class BranchesViewModel(
    accountInstance: AccountInstance,
    login: String,
    repoName: String,
    refPrefix: String
) : ViewModel() {

    private val dataSource by lazy(LazyThreadSafetyMode.NONE) {
        BranchesDataSource(
            apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
            login = login,
            repoName = repoName,
            refPrefix = refPrefix
        )
    }

    val branchesFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = ::dataSource
        ).flow.cachedIn(viewModelScope)
    }

    fun invalidateDataSource(sort: RefOrder) {
        dataSource.sort = sort
        dataSource.invalidate()
    }

}