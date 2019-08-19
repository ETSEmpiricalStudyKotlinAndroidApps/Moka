package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.RepositoryAbstract
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel

class RepositoriesViewModel(
    private val login: String,
    private val repositoryType: RepositoryType
) : NetworkCacheSourceViewModel<RepositoryAbstract>() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<RepositoryAbstract>>>()
    val initialLoadStatus: LiveData<Resource<List<RepositoryAbstract>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource2<List<RepositoryAbstract>>>()
    val pagedLoadStatus: LiveData<PagedResource2<List<RepositoryAbstract>>>
        get() = _pagedLoadStatus

    private lateinit var sourceFactory: RepositoriesDataSourceFactory

    init {
        refresh()
    }

    override fun initRemoteSource(): LiveData<PagedList<RepositoryAbstract>> {
        sourceFactory = RepositoriesDataSourceFactory(
            login,
            repositoryType,
            _initialLoadStatus,
            _pagedLoadStatus
        )

        return LivePagedListBuilder(sourceFactory, pagingConfig)
            .build()
    }

    override fun retryLoadPreviousNext() {
        sourceFactory.retryLoadPreviousNext()
    }

}