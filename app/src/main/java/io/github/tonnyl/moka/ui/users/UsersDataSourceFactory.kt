package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.data.UserGraphQL
import kotlinx.coroutines.CoroutineScope

class UsersDataSourceFactory(
    private val coroutineScope: CoroutineScope,
    private val login: String,
    private val userType: UserType,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<UserGraphQL>>>
) : DataSource.Factory<String, UserGraphQL>() {

    private val followingLiveData by lazy {
        MutableLiveData<FollowingDataSource>()
    }
    private val followersLiveData by lazy {
        MutableLiveData<FollowersDataSource>()
    }

    override fun create(): DataSource<String, UserGraphQL> = when (userType) {
        UserType.FOLLOWER -> FollowersDataSource(coroutineScope, login, loadStatusLiveData).apply {
            followersLiveData.postValue(this)
        }
        UserType.FOLLOWING -> FollowingDataSource(coroutineScope, login, loadStatusLiveData).apply {
            followingLiveData.postValue(this)
        }
    }

    fun invalidate() {
        followingLiveData.value?.invalidate()
        followersLiveData.value?.invalidate()
    }

}