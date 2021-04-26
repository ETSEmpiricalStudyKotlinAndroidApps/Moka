package io.github.tonnyl.moka.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.tonnyl.moka.AccountInstance

class ViewModelFactory(
    private val accountInstance: AccountInstance,
    private val login: String,
    private val profileType: ProfileType
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(
            accountInstance = accountInstance,
            login = login,
            profileType = profileType
        ) as T
    }

}