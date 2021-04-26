package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.tonnyl.moka.AccountInstance

class ViewModelFactory(
    private val accountInstance: AccountInstance,
    private val owner: String,
    private val name: String,
    private val number: Int
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return IssueViewModel(
            accountInstance = accountInstance,
            owner = owner,
            name = name,
            number = number
        ) as T
    }

}