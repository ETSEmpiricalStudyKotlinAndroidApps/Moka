package io.tonnyl.moka.common.db.converter

import androidx.room.TypeConverter
import io.tonnyl.moka.common.db.data.TrendingRepositoryBuiltBy
import io.tonnyl.moka.common.serialization.json
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