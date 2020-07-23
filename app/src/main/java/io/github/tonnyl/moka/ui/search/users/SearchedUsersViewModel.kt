package io.github.tonnyl.moka.ui.search.users

import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.liveData
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.item.SearchedUserItem
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.search.users.SearchedUserItemEvent.*

class SearchedUsersViewModel : ViewModel() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<SearchedUserOrOrgItem>>>()
    val initialLoadStatus: LiveData<Resource<List<SearchedUserOrOrgItem>>>
        get() = _initialLoadStatus

    private val _event = MutableLiveData<Event<SearchedUserItemEvent>>()
    val event: LiveData<Event<SearchedUserItemEvent>>
        get() = _event

    private val queryStringLiveData = MutableLiveData<String>()

    val usersResult = queryStringLiveData.switchMap { queryString ->
        liveData {
            emitSource(
                Pager(
                    config = MokaApp.defaultPagingConfig,
                    pagingSourceFactory = {
                        SearchedUsersItemDataSource(queryString, _initialLoadStatus)
                    }
                ).liveData
            )
        }
    }

    @MainThread
    fun refresh(queryString: String) {
        if (queryString == queryStringLiveData.value
            && initialLoadStatus.value?.status != Status.ERROR
        ) {
            return
        }

        queryStringLiveData.value = queryString
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