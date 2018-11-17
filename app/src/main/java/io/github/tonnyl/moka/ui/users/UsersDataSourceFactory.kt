package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.UserGraphQL

class UsersDataSourceFactory(
        private val login: String,
        private val userType: UserType
) : DataSource.Factory<String, UserGraphQL>() {

    private val followingLiveData by lazy {
        MutableLiveData<FollowingDataSource>()
    }
    private val followersLiveData by lazy {
        MutableLiveData<FollowersDataSource>()
    }

    override fun create(): DataSource<String, UserGraphQL> = when (userType) {
        UserType.FOLLOWER -> FollowersDataSource(login).apply {
            followersLiveData.postValue(this)
        }
        UserType.FOLLOWING -> FollowingDataSource(login).apply {
            followingLiveData.postValue(this)
        }
    }

}