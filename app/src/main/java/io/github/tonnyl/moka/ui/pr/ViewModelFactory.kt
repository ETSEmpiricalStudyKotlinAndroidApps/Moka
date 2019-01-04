package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(
        private val owner: String,
        private val name: String,
        private val number: Int
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = PullRequestViewModel(owner, name, number) as T

}