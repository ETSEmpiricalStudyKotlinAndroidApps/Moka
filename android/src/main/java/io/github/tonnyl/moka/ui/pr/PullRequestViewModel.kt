package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.graphql.PullRequestQuery.PullRequest
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
data class PullRequestViewModelExtra(
    val accountInstance: AccountInstance,
    val owner: String,
    val name: String,
    val number: Int
)

@ExperimentalSerializationApi
class PullRequestViewModel(extra: PullRequestViewModelExtra) : ViewModel() {

    private val _pullRequest = MutableLiveData<PullRequest>()
    val pullRequest: LiveData<PullRequest>
        get() = _pullRequest

    val prTimelineFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                PullRequestTimelineDataSource(
                    apolloClient = extra.accountInstance.apolloGraphQLClient.apolloClient,
                    owner = extra.owner,
                    name = extra.name,
                    number = extra.number,
                    pullRequestData = _pullRequest
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    companion object {

        private object PullRequestViewModelExtraKeyImpl :
            CreationExtras.Key<PullRequestViewModelExtra>

        val PULL_REQUEST_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<PullRequestViewModelExtra> =
            PullRequestViewModelExtraKeyImpl

    }

}