package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(
    private val owner: String,
    private val name: String,
    private val number: Int
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return IssueViewModel(
            owner = owner,
            name = name,
            number = number
        ) as T
    }

}