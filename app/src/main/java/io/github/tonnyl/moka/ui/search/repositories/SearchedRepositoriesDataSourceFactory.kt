package io.github.tonnyl.moka.ui.search.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource

class SearchedRepositoriesDataSourceFactory(
    var keywords: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<SearchedRepositoryItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource<List<SearchedRepositoryItem>>>
) : DataSource.Factory<String, SearchedRepositoryItem>() {

    private var dataSource: SearchedRepositoriesItemDataSource? = null

    override fun create(): DataSource<String, SearchedRepositoryItem> {
        return SearchedRepositoriesItemDataSource(
            keywords,
            initialLoadStatus,
            pagedLoadStatus
        ).also {
            dataSource = it
        }
    }

    fun retryLoadPreviousNext() {
        dataSource?.retry?.invoke()
    }

}