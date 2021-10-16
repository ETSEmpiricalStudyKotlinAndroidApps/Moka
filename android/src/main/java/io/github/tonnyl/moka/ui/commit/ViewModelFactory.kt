package io.github.tonnyl.moka.ui.commit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.tonnyl.moka.AccountInstance
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class ViewModelFactory(
    private val accountInstance: AccountInstance,
    private val owner: String,
    private val repo: String,
    private val ref: String
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CommitViewModel(
            accountInstance = accountInstance,
            owner = owner,
            repo = repo,
            ref = ref
        ) as T
    }

}