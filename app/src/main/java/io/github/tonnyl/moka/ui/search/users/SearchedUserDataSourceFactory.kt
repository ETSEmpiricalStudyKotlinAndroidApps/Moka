package io.github.tonnyl.moka.ui.search.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource

class SearchedUserDataSourceFactory(
    var keywords: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<SearchedUserOrOrgItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource2<List<SearchedUserOrOrgItem>>>
) : DataSource.Factory<String, SearchedUserOrOrgItem>() {

    private var dataSource: SearchedUsersItemDataSource? = null

    override fun create(): DataSource<String, SearchedUserOrOrgItem> {
        return SearchedUsersItemDataSource(keywords, initialLoadStatus, pagedLoadStatus).also {
            dataSource = it
        }
    }

    fun retryLoadPreviousNext() {
        dataSource?.retry?.invoke()
    }

}