package io.github.tonnyl.moka.db.converter

import androidx.room.TypeConverter
import io.github.tonnyl.moka.data.TrendingRepositoryBuiltBy
import io.github.tonnyl.moka.util.json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

object TrendingRepositoryBuiltByListConverters {

    @TypeConverter
    @JvmStatic
    fun trendingRepositoryBuiltByListToString(list: List<TrendingRepositoryBuiltBy>?): String? {
        return list?.let {
            runCatching {
                json.encodeToString(it)
            }.getOrNull()
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromString(jsonString: String?): List<TrendingRepositoryBuiltBy>? {
        return jsonString?.let {
            runCatching {
                json.decodeFromString<List<TrendingRepositoryBuiltBy>>(it)
            }.getOrNull()
        }
    }

}