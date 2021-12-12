package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.tonnyl.moka.common.AccountInstance
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class ViewModelFactory(
    private val accountInstance: AccountInstance,
    private val login: String,
    private val repoName: String?,
    private val usersType: UsersType
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UsersViewModel(
            accountInstance = accountInstance,
            login = login,
            repoName = repoName,
            usersType = usersType
        ) as T
    }

}