package io.github.tonnyl.moka.ui.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.tonnyl.moka.common.AccountInstance
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class ViewModelFactory(
    private val accountInstance: AccountInstance,
    private val name: String?,
    private val bio: String?,
    private val url: String?,
    private val company: String?,
    private val location: String?,
    private val twitter: String?
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditProfileViewModel(
            accountInstance = accountInstance,
            initialName = name,
            initialBio = bio,
            initialUrl = url,
            initialCompany = company,
            initialLocation = location,
            initialTwitter = twitter
        ) as T
    }
}