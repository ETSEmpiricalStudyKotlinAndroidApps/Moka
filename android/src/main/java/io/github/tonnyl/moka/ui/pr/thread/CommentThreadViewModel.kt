package io.github.tonnyl.moka.ui.pr.thread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class CommentThreadViewModel(
    accountInstance: AccountInstance,
    nodeId: String
) : ViewModel() {

    val threadFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                CommentThreadDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    nodeId = nodeId
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}