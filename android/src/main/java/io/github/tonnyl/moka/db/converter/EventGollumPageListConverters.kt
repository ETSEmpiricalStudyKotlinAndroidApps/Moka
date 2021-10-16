package io.github.tonnyl.moka.db.converter

import androidx.room.TypeConverter
import io.github.tonnyl.moka.data.EventGollumPage
import io.tonnyl.moka.common.serialization.json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

object EventGollumPageListConverters {

    @TypeConverter
    @JvmStatic
    fun eventGollumPageListToString(pages: List<EventGollumPage>?): String? {
        return pages?.let {
            runCatching {
                json.encodeToString(it)
            }.getOrNull()
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromString(value: String?): List<EventGollumPage>? {
        return value?.let {
            runCatching {
                json.decodeFromString<List<EventGollumPage>>(it)
            }.getOrNull()
        }
    }

}