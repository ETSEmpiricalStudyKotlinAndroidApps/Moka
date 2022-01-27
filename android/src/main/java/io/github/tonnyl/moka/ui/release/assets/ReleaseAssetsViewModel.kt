package io.github.tonnyl.moka.ui.release.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
data class ReleaseAssetsViewModelExtra(
    val accountInstance: AccountInstance,
    val login: String,
    val repoName: String,
    val tagName: String
)

@ExperimentalSerializationApi
class ReleaseAssetsViewModel(extra: ReleaseAssetsViewModelExtra) : ViewModel() {

    val assets by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                ReleaseAssetsDataSource(
                    apolloClient = extra.accountInstance.apolloGraphQLClient.apolloClient,
                    owner = extra.login,
                    name = extra.repoName,
                    tagName = extra.tagName
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    companion object {

        private object ReleaseAssetsViewModelExtraKeyImpl :
            CreationExtras.Key<ReleaseAssetsViewModelExtra>

        val RELEASES_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<ReleaseAssetsViewModelExtra> =
            ReleaseAssetsViewModelExtraKeyImpl

    }

}