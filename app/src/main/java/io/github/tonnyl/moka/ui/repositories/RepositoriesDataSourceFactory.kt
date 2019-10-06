package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource

class RepositoriesDataSourceFactory(
    private val login: String,
    private val repositoryType: RepositoryType,
    private val loadStatusLiveData: MutableLiveData<Resource<List<RepositoryItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource2<List<RepositoryItem>>>
) : DataSource.Factory<String, RepositoryItem>() {

    private var starredDataSource: StarredRepositoriesDataSource? = null
    private var ownedDataSource: OwnedRepositoriesDataSource? = null

    override fun create(): DataSource<String, RepositoryItem> = when (repositoryType) {
        RepositoryType.STARRED -> {
            StarredRepositoriesDataSource(
                login,
                loadStatusLiveData,
                pagedLoadStatus
            ).also {
                starredDataSource = it
            }
        }
        RepositoryType.OWNED -> {
            OwnedRepositoriesDataSource(
                login,
                loadStatusLiveData,
                pagedLoadStatus
            ).also {
                ownedDataSource = it
            }
        }
    }

    fun retryLoadPreviousNext() {
        starredDataSource?.retry?.invoke()
    }

}