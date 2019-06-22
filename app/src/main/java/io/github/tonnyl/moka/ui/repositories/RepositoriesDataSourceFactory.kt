package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.data.RepositoryAbstract
import kotlinx.coroutines.CoroutineScope

class RepositoriesDataSourceFactory(
    private val coroutineScope: CoroutineScope,
    private val login: String,
    private val repositoryType: RepositoryType,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<RepositoryAbstract>>>
) : DataSource.Factory<String, RepositoryAbstract>() {

    private val ownedRepositoriesLiveData = MutableLiveData<OwnedRepositoriesDataSource>()
    private val starredRepositoriesLiveData = MutableLiveData<StarredRepositoriesDataSource>()

    override fun create(): DataSource<String, RepositoryAbstract> = when (repositoryType) {
        RepositoryType.STARRED -> StarredRepositoriesDataSource(coroutineScope, login, loadStatusLiveData).apply {
            starredRepositoriesLiveData.postValue(this)
        }
        RepositoryType.OWNED -> OwnedRepositoriesDataSource(coroutineScope, login, loadStatusLiveData).apply {
            ownedRepositoriesLiveData.postValue(this)
        }
    }

    fun invalidate() {
        ownedRepositoriesLiveData.value?.invalidate()
        starredRepositoriesLiveData.value?.invalidate()
    }

}