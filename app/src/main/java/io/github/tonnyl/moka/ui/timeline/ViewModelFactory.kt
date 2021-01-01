package io.github.tonnyl.moka.ui.timeline

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi

@ExperimentalPagingApi
class ViewModelFactory(
    private val app: Application
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TimelineViewModel(app) as T
    }

}