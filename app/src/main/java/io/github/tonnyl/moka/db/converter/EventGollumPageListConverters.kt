package io.github.tonnyl.moka.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.tonnyl.moka.data.EventGollumPage

object EventGollumPageListConverters {

    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun eventGollumPageListToString(pages: List<EventGollumPage>?): String? = pages?.let {
        gson.toJson(it)
    }

    @TypeConverter
    @JvmStatic
    fun fromString(value: String?): List<EventGollumPage>? {
        return value?.let {
            val type = object : TypeToken<List<EventGollumPage>>() {}.type
            gson.fromJson(value, type)
        }
    }

}