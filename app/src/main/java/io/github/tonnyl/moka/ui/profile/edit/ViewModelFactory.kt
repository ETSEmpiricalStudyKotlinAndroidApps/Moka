package io.github.tonnyl.moka.ui.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(
    private val name: String?,
    private val bio: String?,
    private val url: String?,
    private val company: String?,
    private val location: String?,
    private val twitter: String?
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditProfileViewModel(name, bio, url, company, location, twitter) as T
    }
}