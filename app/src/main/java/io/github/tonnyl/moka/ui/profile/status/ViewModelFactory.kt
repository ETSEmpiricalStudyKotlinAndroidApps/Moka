package io.github.tonnyl.moka.ui.profile.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(
    private val args: EditStatusFragmentArgs
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditStatusViewModel(args) as T
    }

}