package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.data.IssuePullRequestQueryState
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class ViewModelFactory(
    private val accountInstance: AccountInstance,
    private val owner: String,
    private val name: String,
    private val state: IssuePullRequestQueryState
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PullRequestsViewModel(
            accountInstance = accountInstance,
            owner = owner,
            name = name,
            state = state
        ) as T
    }

}