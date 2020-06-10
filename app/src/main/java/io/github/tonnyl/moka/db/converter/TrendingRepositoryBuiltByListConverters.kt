package io.github.tonnyl.moka.db.converter

import androidx.room.TypeConverter
import io.github.tonnyl.moka.data.TrendingRepositoryBuiltBy
import io.github.tonnyl.moka.util.MoshiInstance

object TrendingRepositoryBuiltByListConverters {

    @TypeConverter
    @JvmStatic
    fun trendingRepositoryBuiltByListToString(list: List<TrendingRepositoryBuiltBy>?): String? {
        return list?.let {
            MoshiInstance.trendingRepositoryBuiltByListAdapter.toJson(it)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromString(jsonString: String?): List<TrendingRepositoryBuiltBy>? {
        return jsonString?.let {
            MoshiInstance.trendingRepositoryBuiltByListAdapter.fromJson(jsonString)
        }
    }

}