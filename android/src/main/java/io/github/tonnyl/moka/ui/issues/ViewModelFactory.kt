package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.tonnyl.moka.AccountInstance
import io.tonnyl.moka.common.data.IssuePullRequestQueryState
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class ViewModelFactory(
    private val accountInstance: AccountInstance,
    private val owner: String,
    private val name: String,
    private val queryState: IssuePullRequestQueryState
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return IssuesViewModel(
            accountInstance = accountInstance,
            owner = owner,
            name = name,
            queryState = queryState
        ) as T
    }

}