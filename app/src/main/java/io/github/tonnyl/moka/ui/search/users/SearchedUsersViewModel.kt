package io.github.tonnyl.moka.ui.search.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem

class SearchedUsersViewModel : ViewModel() {

    private var keywords: String = ""

    private val _loadStatusLiveData = MutableLiveData<PagedResource<List<SearchedUserOrOrgItem>>>()
    val loadStatusLiveData: LiveData<PagedResource<List<SearchedUserOrOrgItem>>>
        get() = _loadStatusLiveData

    private val sourceFactory: SearchedUserDataSourceFactory by lazy {
        SearchedUserDataSourceFactory(viewModelScope, keywords, _loadStatusLiveData)
    }

    val searchedUsersResult: LiveData<PagedList<SearchedUserOrOrgItem>> by lazy {
        val pagingConfig: PagedList.Config = PagedList.Config.Builder()
            .setPageSize(MokaApp.PER_PAGE)
            .setMaxSize(MokaApp.MAX_SIZE_OF_PAGED_LIST)
            .setInitialLoadSizeHint(MokaApp.PER_PAGE)
            .setEnablePlaceholders(false)
            .build()

        LivePagedListBuilder(sourceFactory, pagingConfig).build()
    }

    fun refresh(keywords: String) {
        if (this.keywords != keywords) {
            this.keywords = keywords

            sourceFactory.keywords = this.keywords
            sourceFactory.invalidate()
        }
    }

}