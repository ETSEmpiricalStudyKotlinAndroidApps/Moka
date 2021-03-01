package io.github.tonnyl.moka.ui.timeline

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi

@ExperimentalPagingApi
class ViewModelFactory(
    private val login: String,
    private val userId: Long,
    private val app: Application
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TimelineViewModel(
            login = login,
            userId = userId,
            app = app
        ) as T
    }

}