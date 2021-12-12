package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.graphql.PullRequestQuery.PullRequest
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class PullRequestViewModel(
    accountInstance: AccountInstance,
    owner: String,
    name: String,
    number: Int
) : ViewModel() {

    private val _pullRequest = MutableLiveData<PullRequest>()
    val pullRequest: LiveData<PullRequest>
        get() = _pullRequest

    val prTimelineFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                PullRequestTimelineDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    owner = owner,
                    name = name,
                    number = number,
                    pullRequestData = _pullRequest
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}