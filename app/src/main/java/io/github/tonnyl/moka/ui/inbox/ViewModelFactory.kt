package io.github.tonnyl.moka.ui.inbox

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi

@ExperimentalPagingApi
class ViewModelFactory(
    private val userId: Long,
    private val app: Application
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return InboxViewModel(
            userId = userId,
            app = app
        ) as T
    }

}