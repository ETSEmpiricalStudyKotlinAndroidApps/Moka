package io.github.tonnyl.moka.ui.search.users

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.item.SearchedUserItem
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel
import io.github.tonnyl.moka.ui.search.users.SearchedUserItemEvent.*

class SearchedUsersViewModel : NetworkCacheSourceViewModel<SearchedUserOrOrgItem>() {

    private var keywords: String = ""

    private val _initialLoadStatus = MutableLiveData<Resource<List<SearchedUserOrOrgItem>>>()
    val initialLoadStatus: LiveData<Resource<List<SearchedUserOrOrgItem>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource<List<SearchedUserOrOrgItem>>>()
    val pagedLoadStatus: LiveData<PagedResource<List<SearchedUserOrOrgItem>>>
        get() = _pagedLoadStatus

    private lateinit var sourceFactory: SearchedUserDataSourceFactory

    private val _event = MutableLiveData<Event<SearchedUserItemEvent>>()
    val event: LiveData<Event<SearchedUserItemEvent>>
        get() = _event

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

    @MainThread
    fun viewUserProfile(login: String) {
        _event.value = Event(ViewUserProfile(login))
    }

    @MainThread
    fun viewOrganizationProfile(login: String) {
        _event.value = Event(ViewOrganizationProfile(login))
    }

    @MainThread
    fun followUser(user: SearchedUserItem) {

        _event.value = Event(FollowUserEvent(user))
    }

}