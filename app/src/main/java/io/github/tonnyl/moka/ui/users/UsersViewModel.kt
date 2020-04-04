package io.github.tonnyl.moka.ui.users

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.UserItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.users.ItemUserEvent.ViewProfile

class UsersViewModel(
    private val args: UsersFragmentArgs
) : NetworkCacheSourceViewModel<UserItem>() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<UserItem>>>()
    val initialLoadStatus: LiveData<Resource<List<UserItem>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource<List<UserItem>>>()
    val pagedLoadStatus: LiveData<PagedResource<List<UserItem>>>
        get() = _pagedLoadStatus

    private val _event = MutableLiveData<Event<ItemUserEvent>>()
    val event: LiveData<Event<ItemUserEvent>>
        get() = _event

    private lateinit var sourceFactory: UsersDataSourceFactory

    init {
        refresh()
    }

    override fun initRemoteSource(): LiveData<PagedList<UserItem>> {
        sourceFactory = UsersDataSourceFactory(
            args.login,
            args.usersType,
            _initialLoadStatus,
            _pagedLoadStatus
        )

        return LivePagedListBuilder(sourceFactory, pagingConfig)
            .build()
    }

    override fun retryLoadPreviousNext() {
        sourceFactory.retryLoadPreviousNext()
    }

    @MainThread
    fun viewProfile(login: String) {
        _event.value = Event(ViewProfile(login, ProfileType.USER))
    }

    @MainThread
    fun followUser(login: String) {

    }

}