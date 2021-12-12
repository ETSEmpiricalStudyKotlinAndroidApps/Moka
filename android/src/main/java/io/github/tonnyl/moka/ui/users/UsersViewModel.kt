package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class UsersViewModel(
    accountInstance: AccountInstance,
    login: String,
    repoName: String?,
    usersType: UsersType
) : ViewModel() {

    val usersFlow by lazy {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                UsersDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    login = login,
                    repoName = repoName,
                    usersType = usersType
                )
            }
        ).flow
    }

}