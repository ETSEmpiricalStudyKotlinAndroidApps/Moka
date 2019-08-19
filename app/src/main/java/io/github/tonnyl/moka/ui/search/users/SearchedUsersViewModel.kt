package io.github.tonnyl.moka.ui.search.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel

class SearchedUsersViewModel : NetworkCacheSourceViewModel<SearchedUserOrOrgItem>() {

    private var keywords: String = ""

    private val _initialLoadStatus = MutableLiveData<Resource<List<SearchedUserOrOrgItem>>>()
    val initialLoadStatus: LiveData<Resource<List<SearchedUserOrOrgItem>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource2<List<SearchedUserOrOrgItem>>>()
    val pagedLoadStatus: LiveData<PagedResource2<List<SearchedUserOrOrgItem>>>
        get() = _pagedLoadStatus

    private lateinit var sourceFactory: SearchedUserDataSourceFactory

    override fun initRemoteSource(): LiveData<PagedList<SearchedUserOrOrgItem>> {
        sourceFactory = SearchedUserDataSourceFactory(
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