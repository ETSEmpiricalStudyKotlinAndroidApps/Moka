package io.github.tonnyl.moka.db.converter

import androidx.room.TypeConverter
import io.github.tonnyl.moka.data.EventGollumPage
import io.github.tonnyl.moka.util.MoshiInstance

object EventGollumPageListConverters {

    @TypeConverter
    @JvmStatic
    fun eventGollumPageListToString(pages: List<EventGollumPage>?): String? {
        return pages?.let {
            MoshiInstance.eventGollumPageListAdapter.toJson(it)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromString(value: String?): List<EventGollumPage>? {
        return value?.let {
            MoshiInstance.eventGollumPageListAdapter.fromJson(value)
        }
    }

}