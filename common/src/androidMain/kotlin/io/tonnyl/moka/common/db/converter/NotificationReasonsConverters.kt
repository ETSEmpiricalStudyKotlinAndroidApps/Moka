package io.tonnyl.moka.common.db.converter

import androidx.room.TypeConverter
import io.tonnyl.moka.common.data.NotificationReasons

object NotificationReasonsConverters {

    @TypeConverter
    @JvmStatic
    fun toNotificationReasons(value: String): NotificationReasons {
        return runCatching {
            NotificationReasons.valueOf(value)
        }.getOrDefault(NotificationReasons.OTHER)
    }

    @TypeConverter
    @JvmStatic
    fun fromNotificationReasons(value: NotificationReasons): String {
        return value.name
    }

}