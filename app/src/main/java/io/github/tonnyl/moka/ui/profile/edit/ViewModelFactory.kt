package io.github.tonnyl.moka.ui.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(
    val args: EditProfileFragmentArgs
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditProfileViewModel(args) as T
    }
}