package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp
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
            config = MokaApp.defaultPagingConfig,
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