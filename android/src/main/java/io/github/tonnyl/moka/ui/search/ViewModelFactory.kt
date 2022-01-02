package io.github.tonnyl.moka.ui.search

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.tonnyl.moka.common.AccountInstance
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class ViewModelFactory(
    private val context: Context,
    private val accountInstance: AccountInstance,
    private val initialSearchKeyword: String
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchViewModel(
            app = context.applicationContext as Application,
            accountInstance = accountInstance,
            initialSearchKeyword = initialSearchKeyword
        ) as T
    }

}