package io.github.tonnyl.moka.ui.search.page

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem

class SearchedRepositoriesDataSourceFactory(
        var keywords: String
) : DataSource.Factory<String, SearchedRepositoryItem>() {

    private val searchedRepositoriesLiveData = MutableLiveData<SearchedRepositoriesItemDataSource>()

    override fun create(): DataSource<String, SearchedRepositoryItem> = SearchedRepositoriesItemDataSource(keywords).apply {
        searchedRepositoriesLiveData.postValue(this)
    }

    fun invalidate() {
        searchedRepositoriesLiveData.value?.let {
            it.keywords = this.keywords
            it.invalidate()
        }
    }

}