package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.PagedResource
import io.github.tonnyl.moka.data.UserGraphQL

class UsersDataSourceFactory(
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
        UserType.FOLLOWER -> FollowersDataSource(login, loadStatusLiveData).apply {
            followersLiveData.postValue(this)
        }
        UserType.FOLLOWING -> FollowingDataSource(login, loadStatusLiveData).apply {
            followingLiveData.postValue(this)
        }
    }

    fun invalidate() {
        followingLiveData.value?.invalidate()
        followersLiveData.value?.invalidate()
    }

}