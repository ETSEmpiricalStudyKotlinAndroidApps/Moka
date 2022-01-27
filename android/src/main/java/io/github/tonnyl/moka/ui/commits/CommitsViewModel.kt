package io.github.tonnyl.moka.ui.commits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
data class CommitsViewModelExtra(
    val accountInstance: AccountInstance,
    val login: String,
    val repoName: String,
    val qualifiedName: String
)

@ExperimentalSerializationApi
class CommitsViewModel(extra: CommitsViewModelExtra) : ViewModel() {

    val commitsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                CommitsDataSource(
                    apolloClient = extra.accountInstance.apolloGraphQLClient.apolloClient,
                    login = extra.login,
                    repoName = extra.repoName,
                    qualifiedName = extra.qualifiedName
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    companion object {

        private object CommitsViewModelExtraKeyImpl : CreationExtras.Key<CommitsViewModelExtra>

        val COMMITS_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<CommitsViewModelExtra> =
            CommitsViewModelExtraKeyImpl

    }

}