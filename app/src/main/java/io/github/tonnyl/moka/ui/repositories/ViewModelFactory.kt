package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.tonnyl.moka.AccountInstance

class ViewModelFactory(
    private val accountInstance: AccountInstance,
    private val login: String,
    private val repositoryType: RepositoryType
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RepositoriesViewModel(
            accountInstance = accountInstance,
            login = login,
            repositoryType = repositoryType
        ) as T
    }

}