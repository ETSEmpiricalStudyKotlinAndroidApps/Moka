package io.github.tonnyl.moka.ui.inbox

import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.ui.profile.ProfileType

sealed class NotificationItemEvent {

    data class ViewNotification(val notification: Notification) : NotificationItemEvent()

    data class ViewProfile(
        val login: String,
        val type: ProfileType
    ) : NotificationItemEvent()

    data class ViewRepository(
        val login: String,
        val name: String,
        val type: ProfileType
    ) : NotificationItemEvent()

}