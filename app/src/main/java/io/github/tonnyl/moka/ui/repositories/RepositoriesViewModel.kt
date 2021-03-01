package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.MokaApp

class RepositoriesViewModel(
    login: String,
    repositoryType: RepositoryType
) : ViewModel() {

    val repositoriesFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                when (repositoryType) {
                    RepositoryType.STARRED -> {
                        StarredRepositoriesDataSource(login)
                    }
                    RepositoryType.OWNED -> {
                        OwnedRepositoriesDataSource(login)
                    }
                }
            }
        ).flow.cachedIn(viewModelScope)
    }

}