package io.github.tonnyl.moka.ui.release.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class ReleaseAssetsViewModel(
    private val accountInstance: AccountInstance,
    private val login: String,
    private val repoName: String,
    private val tagName: String
) : ViewModel() {

    val assets by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                ReleaseAssetsDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    owner = login,
                    name = repoName,
                    tagName = tagName
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}