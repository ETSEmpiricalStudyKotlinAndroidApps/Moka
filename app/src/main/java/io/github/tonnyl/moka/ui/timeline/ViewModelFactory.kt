package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.tonnyl.moka.db.dao.EventDao

class ViewModelFactory(
    private val localSource: EventDao
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TimelineViewModel(localSource) as T
    }

}