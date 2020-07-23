package io.github.tonnyl.moka.ui.projects

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(
    private val isMyself: Boolean,
    private val args: ProjectsFragmentArgs,
    private val app: Application
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProjectsViewModel(isMyself, args, app) as T
    }

}