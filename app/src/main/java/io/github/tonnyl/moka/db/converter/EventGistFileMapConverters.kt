package io.github.tonnyl.moka.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.tonnyl.moka.data.EventGistFile

object EventGistFileMapConverters {

    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun eventGistFileListToString(map: Map<String, EventGistFile>): String = gson.toJson(map)

    @TypeConverter
    @JvmStatic
    fun fromString(jsonString: String?): Map<String, EventGistFile>? {
        val type = object : TypeToken<Map<String, EventGistFile>>() {}.type
        return gson.fromJson(jsonString, type)
    }

}