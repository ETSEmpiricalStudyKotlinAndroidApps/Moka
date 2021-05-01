package io.github.tonnyl.moka.serializers.store.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@ExperimentalSerializationApi
@Serializable
data class Settings(

    @ProtoNumber(1)
    // App appearance.
    val theme: Theme = Theme.AUTO,

    @ProtoNumber(2)
    // Fetch unread notifications on the background or not.
    val enableNotifications: Boolean = true,

    @ProtoNumber(3)
    // Time period of fetching unread notifications.
    val notificationSyncInterval: NotificationSyncInterval = NotificationSyncInterval.ONE_QUARTER,

    @ProtoNumber(4)
    // Do not disturb.
    val dnd: Boolean = false,

    @ProtoNumber(5)
    // Keep user's search history.
    val doNotKeepSearchHistory: Boolean = true,

    @ProtoNumber(6)
    // Time period of keeping local data.
    val keepData: KeepData = KeepData.FOREVER,

    @ProtoNumber(7)
    // Save user's input automatically or not.
    val autoSave: Boolean = true

)

@ExperimentalSerializationApi
@Serializable
enum class Theme {

    @ProtoNumber(0)
    AUTO,

    @ProtoNumber(1)
    LIGHT,

    @ProtoNumber(2)
    DARK,

}

@ExperimentalSerializationApi
@Serializable
enum class NotificationSyncInterval {

    @ProtoNumber(0)
    ONE_QUARTER,

    @ProtoNumber(1)
    THIRTY_MINUTES,

    @ProtoNumber(2)
    ONE_HOUR,

    @ProtoNumber(3)
    TWO_HOURS,

    @ProtoNumber(4)
    SIX_HOURS,

    @ProtoNumber(5)
    TWELVE_HOURS,

    @ProtoNumber(6)
    TWENTY_FOUR_HOURS,

}

@ExperimentalSerializationApi
@Serializable
enum class KeepData {

    @ProtoNumber(0)
    FOREVER,

    @ProtoNumber(1)
    ONE_DAY,

    @ProtoNumber(2)
    THREE_DAYS,

    @ProtoNumber(3)
    ONE_WEEK,

    @ProtoNumber(4)
    ONE_MONTH,

}