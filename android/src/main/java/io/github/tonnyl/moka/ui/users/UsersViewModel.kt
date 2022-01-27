package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
data class UsersViewModelExtra(
    val accountInstance: AccountInstance,
    val login: String,
    val repoName: String?,
    val usersType: UsersType
)

@ExperimentalSerializationApi
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

    companion object {

        private object UsersViewModelExtraKeyImpl : CreationExtras.Key<UsersViewModelExtra>

        val USERS_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<UsersViewModelExtra> =
            UsersViewModelExtraKeyImpl

    }

}