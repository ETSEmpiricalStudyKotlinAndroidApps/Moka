package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.PagedResource
import io.github.tonnyl.moka.data.RepositoryAbstract

class RepositoriesViewModel(
        private val login: String,
        private val repositoryType: RepositoryType
) : ViewModel() {

    private val _loadStatusLiveData = MutableLiveData<PagedResource<List<RepositoryAbstract>>>()
    val loadStatusLiveData: LiveData<PagedResource<List<RepositoryAbstract>>>
        get() = _loadStatusLiveData

    private val sourceFactory = RepositoriesDataSourceFactory(login, repositoryType, _loadStatusLiveData)

    val repositoriesResults: LiveData<PagedList<RepositoryAbstract>> by lazy {
        val config = PagedList.Config.Builder()
                .setPageSize(20)
                .setInitialLoadSizeHint(20 * 1)
                .setEnablePlaceholders(false)
                .build()

        LivePagedListBuilder(sourceFactory, config).build()
    }

    fun refresh() {
        sourceFactory.invalidate()
    }

}