package io.github.tonnyl.moka.ui.users

import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.liveData
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.UserItem
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.users.ItemUserEvent.ViewProfile

class UsersViewModel(
    private val args: UsersFragmentArgs
) : ViewModel() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<UserItem>>>()
    val initialLoadStatus: LiveData<Resource<List<UserItem>>>
        get() = _initialLoadStatus

    private val _event = MutableLiveData<Event<ItemUserEvent>>()
    val event: LiveData<Event<ItemUserEvent>>
        get() = _event

    val usersResult = liveData {
        emitSource(
            Pager(
                config = MokaApp.defaultPagingConfig,
                pagingSourceFactory = {
                    when (args.usersType) {
                        UsersType.FOLLOWER -> {
                            FollowingDataSource(args.login, _initialLoadStatus)
                        }
                        UsersType.FOLLOWING -> {
                            FollowersDataSource(args.login, _initialLoadStatus)
                        }
                    }
                }
            ).liveData
        )
    }.cachedIn(viewModelScope)

    @MainThread
    fun viewProfile(login: String) {
        _event.value = Event(ViewProfile(login, ProfileType.USER))
    }

    @MainThread
    fun followUser(login: String) {

    }

}