package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp
import io.tonnyl.moka.graphql.IssueQuery.Issue
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class IssueViewModel(
    accountInstance: AccountInstance,
    owner: String,
    name: String,
    number: Int
) : ViewModel() {

    private val _issueLiveData = MutableLiveData<Issue>()
    val issueLiveData: LiveData<Issue>
        get() = _issueLiveData

    val issueTimelineFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                IssueTimelineDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    owner = owner,
                    name = name,
                    number = number,
                    issueData = _issueLiveData
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}