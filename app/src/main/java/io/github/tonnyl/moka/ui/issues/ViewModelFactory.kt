package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(
    private val owner: String,
    private val name: String
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return IssuesViewModel(owner = owner, name = name) as T
    }

}