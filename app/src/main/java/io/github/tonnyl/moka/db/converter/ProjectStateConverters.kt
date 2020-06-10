package io.github.tonnyl.moka.db.converter

import androidx.room.TypeConverter
import io.github.tonnyl.moka.type.ProjectState

object ProjectStateConverters {

    @TypeConverter
    @JvmStatic
    fun projectStateToLong(state: ProjectState): String {
        return state.rawValue
    }

    @TypeConverter
    @JvmStatic
    fun fromOrdinal(value: String): ProjectState = ProjectState.safeValueOf(value)

}