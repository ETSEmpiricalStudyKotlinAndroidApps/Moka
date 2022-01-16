package io.github.tonnyl.moka.ui.file

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.tonnyl.moka.common.AccountInstance
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class ViewModelFactory(
    private val context: Context,
    private val accountInstance: AccountInstance,
    private val url: String,
    private val filename: String,
    private val fileExtension: String?
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FileViewModel(
            accountInstance = accountInstance,
            url = url,
            filename = filename,
            fileExtension = fileExtension,
            context = context
        ) as T
    }

}