package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.graphql.IssueQuery.Issue
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
data class IssueViewModelExtra(
    val accountInstance: AccountInstance,
    val owner: String,
    val name: String,
    val number: Int
)

@ExperimentalSerializationApi
class IssueViewModel(extra: IssueViewModelExtra) : ViewModel() {

    private val _issueLiveData = MutableLiveData<Issue>()
    val issueLiveData: LiveData<Issue>
        get() = _issueLiveData

    val issueTimelineFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                IssueTimelineDataSource(
                    apolloClient = extra.accountInstance.apolloGraphQLClient.apolloClient,
                    owner = extra.owner,
                    name = extra.name,
                    number = extra.number,
                    issueData = _issueLiveData
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    companion object {

        private object IssueViewModelExtraKeyImpl : CreationExtras.Key<IssueViewModelExtra>

        val ISSUE_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<IssueViewModelExtra> =
            IssueViewModelExtraKeyImpl

    }

}