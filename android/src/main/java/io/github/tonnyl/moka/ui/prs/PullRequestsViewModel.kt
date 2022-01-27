package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.data.IssuePullRequestQueryState
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
data class PullRequestsViewModelExtra(
    val accountInstance: AccountInstance,
    val owner: String,
    val name: String,
    val state: IssuePullRequestQueryState
)

@ExperimentalSerializationApi
class PullRequestsViewModel(extra: PullRequestsViewModelExtra) : ViewModel() {

    val prsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                PullRequestsDataSource(
                    api = extra.accountInstance.repositoryApi,
                    owner = extra.owner,
                    name = extra.name,
                    queryState = extra.state
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    companion object {

        private object PullRequestsViewModelExtraKeyImpl :
            CreationExtras.Key<PullRequestsViewModelExtra>

        val PULL_REQUESTS_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<PullRequestsViewModelExtra> =
            PullRequestsViewModelExtraKeyImpl

    }

}