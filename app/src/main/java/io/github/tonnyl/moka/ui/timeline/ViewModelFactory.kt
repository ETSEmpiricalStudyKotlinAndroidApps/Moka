package io.github.tonnyl.moka.ui.timeline

import android.util.ArrayMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory constructor(
        private val creators: ArrayMap<Class<out ViewModel>, ViewModel>
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = EventsViewModel() as T

}