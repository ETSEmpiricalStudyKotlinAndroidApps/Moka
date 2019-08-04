package io.github.tonnyl.moka.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.tonnyl.moka.data.TrendingRepositoryBuiltBy

object TrendingRepositoryBuiltByListConverters {

    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun trendingRepositoryBuiltByListToString(list: List<TrendingRepositoryBuiltBy>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    @JvmStatic
    fun fromString(jsonString: String): List<TrendingRepositoryBuiltBy> {
        val type = object : TypeToken<List<TrendingRepositoryBuiltBy>>() {}.type
        return gson.fromJson(jsonString, type)
    }

}