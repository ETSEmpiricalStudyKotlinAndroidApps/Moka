package io.github.tonnyl.moka.ui.branches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.ui.defaultPagingConfig
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.graphql.type.RefOrder

data class BranchesViewModelExtra(
    val accountInstance: AccountInstance,
    val login: String,
    val repoName: String,
    val refPrefix: String
)

class BranchesViewModel(extra: BranchesViewModelExtra) : ViewModel() {

    private val dataSource by lazy(LazyThreadSafetyMode.NONE) {
        BranchesDataSource(
            apolloClient = extra.accountInstance.apolloGraphQLClient.apolloClient,
            login = extra.login,
            repoName = extra.repoName,
            refPrefix = extra.refPrefix
        )
    }

    val branchesFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = ::dataSource
        ).flow.cachedIn(viewModelScope)
    }

    fun invalidateDataSource(sort: RefOrder) {
        dataSource.sort = sort
        dataSource.invalidate()
    }

}