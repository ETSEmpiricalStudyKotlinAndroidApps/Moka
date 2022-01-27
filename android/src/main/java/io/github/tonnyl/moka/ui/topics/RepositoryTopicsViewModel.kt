package io.github.tonnyl.moka.ui.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
data class RepositoryTopicsViewModelExtra(
    val accountInstance: AccountInstance,
    val login: String,
    val repoName: String
)

@ExperimentalSerializationApi
class RepositoryTopicsViewModel(extra: RepositoryTopicsViewModelExtra) : ViewModel() {

    val topicsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                RepositoryTopicsDataSource(
                    apolloClient = extra.accountInstance.apolloGraphQLClient.apolloClient,
                    login = extra.login,
                    name = extra.repoName
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    companion object {

        private object RepositoryTopicsViewModelExtraKeyImpl :
            CreationExtras.Key<RepositoryTopicsViewModelExtra>

        val REPOSITORY_TOPICS_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<RepositoryTopicsViewModelExtra> =
            RepositoryTopicsViewModelExtraKeyImpl

    }

}