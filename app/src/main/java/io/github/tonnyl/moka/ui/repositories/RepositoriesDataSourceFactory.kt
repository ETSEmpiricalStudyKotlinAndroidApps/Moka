package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.RepositoryAbstract

class RepositoriesDataSourceFactory(
        private val login: String,
        private val repositoryType: RepositoryType
) : DataSource.Factory<String, RepositoryAbstract>() {

    private val ownedRepositoriesLiveData = MutableLiveData<OwnedRepositoriesDataSource>()
    private val starredRepositoriesLiveData = MutableLiveData<StarredRepositoriesDataSource>()

    override fun create(): DataSource<String, RepositoryAbstract> = when (repositoryType) {
        RepositoryType.STARRED -> StarredRepositoriesDataSource(login).apply {
            starredRepositoriesLiveData.postValue(this)
        }
        RepositoryType.OWNED -> OwnedRepositoriesDataSource(login).apply {
            ownedRepositoriesLiveData.postValue(this)
        }
    }

}