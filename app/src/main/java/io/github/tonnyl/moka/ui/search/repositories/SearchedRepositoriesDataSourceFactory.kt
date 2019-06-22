package io.github.tonnyl.moka.ui.search.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem
import kotlinx.coroutines.CoroutineScope

class SearchedRepositoriesDataSourceFactory(
    private val coroutineScope: CoroutineScope,
    var keywords: String,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<SearchedRepositoryItem>>>
) : DataSource.Factory<String, SearchedRepositoryItem>() {

    private val searchedRepositoriesLiveData = MutableLiveData<SearchedRepositoriesItemDataSource>()

    override fun create(): DataSource<String, SearchedRepositoryItem> {
        return SearchedRepositoriesItemDataSource(coroutineScope, keywords, loadStatusLiveData).apply {
            searchedRepositoriesLiveData.postValue(this)
        }
    }

    fun invalidate() {
        searchedRepositoriesLiveData.value?.let {
            it.keywords = this.keywords
            it.invalidate()
        }
    }

}