package io.github.tonnyl.moka.ui.releases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class ReleasesViewModel(
    accountInstance: AccountInstance,
    login: String,
    repoName: String
) : ViewModel() {

    val releasesFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                ReleasesDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    owner = login,
                    name = repoName
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}