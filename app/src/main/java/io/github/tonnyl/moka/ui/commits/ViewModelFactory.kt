package io.github.tonnyl.moka.ui.commits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.tonnyl.moka.AccountInstance
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class ViewModelFactory(
    private val accountInstance: AccountInstance,
    private val isOrg: Boolean,
    private val login: String,
    private val repoName: String
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CommitsViewModel(
            accountInstance = accountInstance,
            isOrg = isOrg,
            login = login,
            repoName = repoName
        ) as T
    }

}