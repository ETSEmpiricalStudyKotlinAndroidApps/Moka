package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp

class UsersViewModel(
    accountInstance: AccountInstance,
    login: String,
    usersType: UsersType
) : ViewModel() {

    val usersFlow by lazy {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                when (usersType) {
                    UsersType.FOLLOWER -> {
                        FollowersDataSource(
                            apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                            login = login
                        )
                    }
                    UsersType.FOLLOWING -> {
                        FollowingDataSource(
                            apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                            login = login
                        )
                    }
                }
            }
        ).flow
    }

}