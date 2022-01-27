package io.github.tonnyl.moka.ui.releases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
data class ReleasesViewModelExtra(
    val accountInstance: AccountInstance,
    val login: String,
    val repoName: String
)

@ExperimentalSerializationApi
class ReleasesViewModel(extra: ReleasesViewModelExtra) : ViewModel() {

    val releasesFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                ReleasesDataSource(
                    apolloClient = extra.accountInstance.apolloGraphQLClient.apolloClient,
                    owner = extra.login,
                    name = extra.repoName
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    companion object {

        private object ReleasesViewModelExtraKeyImpl : CreationExtras.Key<ReleasesViewModelExtra>

        val RELEASES_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<ReleasesViewModelExtra> =
            ReleasesViewModelExtraKeyImpl

    }

}