package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.UserItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource

class UsersDataSourceFactory(
    private val login: String,
    private val usersType: UsersType,
    private val initialLoadStatus: MutableLiveData<Resource<List<UserItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource<List<UserItem>>>
) : DataSource.Factory<String, UserItem>() {

    private var followingDataSource: FollowingDataSource? = null
    private var followersDataSource: FollowersDataSource? = null

    override fun create(): DataSource<String, UserItem> = when (usersType) {
        UsersType.FOLLOWER -> FollowersDataSource(
            login,
            initialLoadStatus,
            pagedLoadStatus
        ).also {
            followersDataSource = it
        }
        UsersType.FOLLOWING -> FollowingDataSource(
            login,
            initialLoadStatus,
            pagedLoadStatus
        ).also {
            followingDataSource = it
        }
    }

    fun retryLoadPreviousNext() {
        followingDataSource?.retry?.invoke()
        followersDataSource?.retry?.invoke()
    }

}