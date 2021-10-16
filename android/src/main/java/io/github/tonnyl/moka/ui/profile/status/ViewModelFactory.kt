package io.github.tonnyl.moka.ui.profile.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.tonnyl.moka.AccountInstance
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class ViewModelFactory(
    private val accountInstance: AccountInstance,
    private val emoji: String?,
    private val message: String?,
    private val indicatesLimitedAvailability: Boolean?
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditStatusViewModel(
            accountInstance = accountInstance,
            initialEmoji = emoji,
            initialMessage = message,
            initialIndicatesLimitedAvailability = indicatesLimitedAvailability
        ) as T
    }

}