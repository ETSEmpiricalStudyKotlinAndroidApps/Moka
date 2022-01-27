package io.github.tonnyl.moka.ui.pr.thread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
data class CommentThreadViewModelExtra(
    val accountInstance: AccountInstance,
    val nodeId: String
)

@ExperimentalSerializationApi
class CommentThreadViewModel(extra: CommentThreadViewModelExtra) : ViewModel() {

    val threadFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                CommentThreadDataSource(
                    apolloClient = extra.accountInstance.apolloGraphQLClient.apolloClient,
                    nodeId = extra.nodeId
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    companion object {

        private object CommentThreadViewModelExtraKeyImpl :
            CreationExtras.Key<CommentThreadViewModelExtra>

        val COMMENT_THREAD_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<CommentThreadViewModelExtra> =
            CommentThreadViewModelExtraKeyImpl

    }

}