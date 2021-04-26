package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp

class IssuesViewModel(
    accountInstance: AccountInstance,
    owner: String,
    name: String
) : ViewModel() {

    val issuesFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                IssuesDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    owner = owner,
                    name = name
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}