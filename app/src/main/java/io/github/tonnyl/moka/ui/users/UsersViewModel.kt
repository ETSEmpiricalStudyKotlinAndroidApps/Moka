package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import io.github.tonnyl.moka.MokaApp

class UsersViewModel(
    login: String,
    usersType: UsersType
) : ViewModel() {

    val usersFlow by lazy {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                when (usersType) {
                    UsersType.FOLLOWER -> {
                        FollowersDataSource(login)
                    }
                    UsersType.FOLLOWING -> {
                        FollowingDataSource(login)
                    }
                }
            }
        ).flow
    }

}