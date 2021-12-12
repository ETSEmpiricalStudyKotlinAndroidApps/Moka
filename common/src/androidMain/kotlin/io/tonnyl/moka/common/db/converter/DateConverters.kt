package io.tonnyl.moka.common.db.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

object DateConverters {

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(instant: Instant?): Long? = instant?.toEpochMilliseconds()

    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Instant? = value?.let {
        Instant.fromEpochMilliseconds(it)
    }

}