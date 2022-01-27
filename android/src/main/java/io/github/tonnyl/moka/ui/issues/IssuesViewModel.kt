package io.github.tonnyl.moka.ui.issues

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
data class IssuesViewModelExtra(
    val accountInstance: AccountInstance,
    val owner: String,
    val name: String,
    val queryState: IssuePullRequestQueryState
)

@ExperimentalSerializationApi
class IssuesViewModel(extra: IssuesViewModelExtra) : ViewModel() {

    val issuesFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                IssuesDataSource(
                    api = extra.accountInstance.repositoryApi,
                    owner = extra.owner,
                    name = extra.name,
                    queryState = extra.queryState
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    companion object {

        private object IssuesViewModelExtraKeyImpl : CreationExtras.Key<IssuesViewModelExtra>

        val ISSUES_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<IssuesViewModelExtra> =
            IssuesViewModelExtraKeyImpl

    }

}