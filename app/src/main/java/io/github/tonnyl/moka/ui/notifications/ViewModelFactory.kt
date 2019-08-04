package io.github.tonnyl.moka.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.tonnyl.moka.db.dao.NotificationDao

class ViewModelFactory(
    private val notificationDao: NotificationDao
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NotificationsViewModel(notificationDao) as T
    }

}