package io.github.tonnyl.moka.ui.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(
        private val login: String,
        private val repositoryName: String
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = RepositoryViewModel(login, repositoryName) as T

}