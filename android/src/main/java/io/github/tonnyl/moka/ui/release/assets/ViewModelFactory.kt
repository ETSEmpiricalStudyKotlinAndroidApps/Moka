package io.github.tonnyl.moka.ui.release.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.tonnyl.moka.AccountInstance
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class ViewModelFactory(
    private val accountInstance: AccountInstance,
    private val login: String,
    private val repoName: String,
    private val tagName: String
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ReleaseAssetsViewModel(
            accountInstance = accountInstance,
            login = login,
            repoName = repoName,
            tagName = tagName
        ) as T
    }

}