package io.github.tonnyl.moka.ui.inbox

import io.github.tonnyl.moka.data.Notification

interface InboxActions {

    fun openNotification(notification: Notification)

}