package io.github.tonnyl.moka.ui.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.tonnyl.moka.ui.profile.ProfileType

class ViewModelFactory(
    private val login: String,
    private val repositoryName: String,
    private val profileType: ProfileType
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RepositoryViewModel(login, repositoryName, profileType) as T
    }

}