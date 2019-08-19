package io.github.tonnyl.moka.ui.search.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel

class SearchedRepositoriesViewModel : NetworkCacheSourceViewModel<SearchedRepositoryItem>() {

    private var keywords: String = ""

    private val _initialLoadStatus = MutableLiveData<Resource<List<SearchedRepositoryItem>>>()
    val initialLoadStatus: LiveData<Resource<List<SearchedRepositoryItem>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource2<List<SearchedRepositoryItem>>>()
    val pagedLoadStatus: LiveData<PagedResource2<List<SearchedRepositoryItem>>>
        get() = _pagedLoadStatus

    private lateinit var sourceFactory: SearchedRepositoriesDataSourceFactory

    override fun initRemoteSource(): LiveData<PagedList<SearchedRepositoryItem>> {
        sourceFactory = SearchedRepositoriesDataSourceFactory(
            keywords,
            _initialLoadStatus,
            _pagedLoadStatus
        )

        return LivePagedListBuilder(sourceFactory, pagingConfig)
            .build()
    }

    override fun retryLoadPreviousNext() {
        sourceFactory.retryLoadPreviousNext()
    }

    fun refresh(keywords: String) {
        if (this.keywords != keywords) {
            this.keywords = keywords

            refresh()
        }
    }
}