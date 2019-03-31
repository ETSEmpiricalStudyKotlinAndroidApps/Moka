package io.github.tonnyl.moka.ui.search.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem

class SearchedUserDataSourceFactory(
        var keywords: String
) : DataSource.Factory<String, SearchedUserOrOrgItem>() {

    private val searchedUserLiveData = MutableLiveData<SearchedUsersItemDataSource>()

    override fun create(): DataSource<String, SearchedUserOrOrgItem> = SearchedUsersItemDataSource(keywords).apply {
        searchedUserLiveData.postValue(this)
    }

    fun invalidate() {
        searchedUserLiveData.value?.let {
            it.keywords = this.keywords
            it.invalidate()
        }
    }

}