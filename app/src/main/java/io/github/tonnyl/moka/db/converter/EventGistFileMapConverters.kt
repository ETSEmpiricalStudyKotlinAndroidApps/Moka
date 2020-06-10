package io.github.tonnyl.moka.db.converter

import androidx.room.TypeConverter
import io.github.tonnyl.moka.data.EventGistFile
import io.github.tonnyl.moka.util.MoshiInstance

object EventGistFileMapConverters {

    @TypeConverter
    @JvmStatic
    fun eventGistFileListToString(map: Map<String, EventGistFile>?): String? {
        return map?.let {
            MoshiInstance.eventGistFileMapAdapter.toJson(it)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromString(jsonString: String?): Map<String, EventGistFile>? {
        return jsonString?.let {
            MoshiInstance.eventGistFileMapAdapter.fromJson(jsonString)
        }
    }

}