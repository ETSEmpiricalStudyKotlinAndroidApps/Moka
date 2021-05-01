package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class PullRequestsViewModel(
    accountInstance: AccountInstance,
    owner: String,
    name: String
) : ViewModel() {

    val prsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                PullRequestsDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    owner = owner,
                    name = name
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}