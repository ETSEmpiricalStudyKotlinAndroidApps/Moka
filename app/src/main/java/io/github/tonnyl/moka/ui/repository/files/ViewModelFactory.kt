package io.github.tonnyl.moka.ui.repository.files

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.tonnyl.moka.AccountInstance
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class ViewModelFactory(
    private val accountInstance: AccountInstance,
    private val login: String,
    private val repositoryName: String,
    private val expression: String
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RepositoryFilesViewModel(
            accountInstance = accountInstance,
            login = login,
            repositoryName = repositoryName,
            expression = expression
        ) as T
    }

}