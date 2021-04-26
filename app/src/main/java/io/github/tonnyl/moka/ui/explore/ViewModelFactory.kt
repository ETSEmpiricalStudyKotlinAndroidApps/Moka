package io.github.tonnyl.moka.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.tonnyl.moka.AccountInstance

class ViewModelFactory(
    private val accountInstance: AccountInstance
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ExploreViewModel(accountInstance = accountInstance) as T
    }

}