package io.github.tonnyl.moka.ui.commits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig

data class CommitsViewModelExtra(
    val accountInstance: AccountInstance,
    val login: String,
    val repoName: String,
    val qualifiedName: String
)

class CommitsViewModel(extra: CommitsViewModelExtra) : ViewModel() {

    val commitsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                CommitsDataSource(
                    apolloClient = extra.accountInstance.apolloGraphQLClient.apolloClient,
                    login = extra.login,
                    repoName = extra.repoName,
                    qualifiedName = extra.qualifiedName
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}