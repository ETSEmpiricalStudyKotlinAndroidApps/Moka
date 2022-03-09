package io.github.tonnyl.moka.ui.releases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig

data class ReleasesViewModelExtra(
    val accountInstance: AccountInstance,
    val login: String,
    val repoName: String
)

class ReleasesViewModel(extra: ReleasesViewModelExtra) : ViewModel() {

    val releasesFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                ReleasesDataSource(
                    apolloClient = extra.accountInstance.apolloGraphQLClient.apolloClient,
                    owner = extra.login,
                    name = extra.repoName
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}