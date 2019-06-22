package io.github.tonnyl.moka.ui.search.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import kotlinx.coroutines.CoroutineScope

class SearchedUserDataSourceFactory(
    private val coroutineScope: CoroutineScope,
    var keywords: String,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<SearchedUserOrOrgItem>>>
) : DataSource.Factory<String, SearchedUserOrOrgItem>() {

    private val searchedUserLiveData = MutableLiveData<SearchedUsersItemDataSource>()

    override fun create(): DataSource<String, SearchedUserOrOrgItem> {
        return SearchedUsersItemDataSource(coroutineScope, keywords, loadStatusLiveData).apply {
            searchedUserLiveData.postValue(this)
        }
    }

    fun invalidate() {
        searchedUserLiveData.value?.let {
            it.keywords = this.keywords
            it.invalidate()
        }
    }

}