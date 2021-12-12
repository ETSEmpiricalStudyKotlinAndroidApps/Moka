package io.tonnyl.moka.common.db.converter

import androidx.room.TypeConverter
import io.tonnyl.moka.common.db.data.EventGistFile
import io.tonnyl.moka.common.serialization.json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

object EventGistFileMapConverters {

    @TypeConverter
    @JvmStatic
    fun eventGistFileListToString(map: Map<String, EventGistFile>?): String? {
        return map?.let {
            runCatching {
                json.encodeToString(it)
            }.getOrNull()
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromString(jsonString: String?): Map<String, EventGistFile>? {
        return jsonString?.let {
            runCatching {
                json.decodeFromString<Map<String, EventGistFile>>(it)
            }.getOrNull()
        }
    }

}