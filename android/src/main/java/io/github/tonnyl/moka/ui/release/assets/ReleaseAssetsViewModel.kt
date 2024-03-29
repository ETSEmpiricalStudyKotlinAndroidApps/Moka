package io.github.tonnyl.moka.ui.release.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.ui.defaultPagingConfig
import io.tonnyl.moka.common.AccountInstance

data class ReleaseAssetsViewModelExtra(
    val accountInstance: AccountInstance,
    val login: String,
    val repoName: String,
    val tagName: String
)

class ReleaseAssetsViewModel(extra: ReleaseAssetsViewModelExtra) : ViewModel() {

    val assets by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                ReleaseAssetsDataSource(
                    apolloClient = extra.accountInstance.apolloGraphQLClient.apolloClient,
                    owner = extra.login,
                    name = extra.repoName,
                    tagName = extra.tagName
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}