package io.github.tonnyl.moka.db.converter

import androidx.room.TypeConverter
import io.github.tonnyl.moka.data.NotificationReasons

object NotificationReasonsConverters {

    @TypeConverter
    @JvmStatic
    fun toNotificationReasons(value: String): NotificationReasons {
        return NotificationReasons.valueOf(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromNotificationReasons(value: NotificationReasons): String {
        return value.name
    }

}