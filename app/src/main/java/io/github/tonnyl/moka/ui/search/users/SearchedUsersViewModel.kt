package io.github.tonnyl.moka.ui.search.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem

class SearchedUsersViewModel : ViewModel() {

    private var keywords: String = ""

    private val sourceFactory: SearchedUserDataSourceFactory by lazy {
        SearchedUserDataSourceFactory(keywords)
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