package io.github.tonnyl.moka.ui.notifications

import io.github.tonnyl.moka.data.Notification

interface NotificationActions {

    fun openNotification(notification: Notification)

}