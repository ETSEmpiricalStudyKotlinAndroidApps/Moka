package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimelineViewModel::class.java)) {
            return TimelineViewModel() as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}