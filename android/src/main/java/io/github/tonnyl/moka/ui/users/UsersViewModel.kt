package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import io.github.tonnyl.moka.ui.defaultPagingConfig
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.data.UsersType

data class UsersViewModelExtra(
    val accountInstance: AccountInstance,
    val login: String,
    val repoName: String?,
    val usersType: UsersType
)

class UsersViewModel(extra: UsersViewModelExtra) : ViewModel() {

    val usersFlow by lazy {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                UsersDataSource(
                    apolloClient = extra.accountInstance.apolloGraphQLClient.apolloClient,
                    login = extra.login,
                    repoName = extra.repoName,
                    usersType = extra.usersType
                )
            }
        ).flow
    }

}