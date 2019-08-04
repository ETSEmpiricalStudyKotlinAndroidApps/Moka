package io.github.tonnyl.moka.db.converter

import androidx.room.TypeConverter
import java.util.*

object DateConverters {

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Date? = value?.let {
        Date(it)
    }

}