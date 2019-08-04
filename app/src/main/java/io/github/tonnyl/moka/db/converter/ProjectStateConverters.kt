package io.github.tonnyl.moka.db.converter

import androidx.room.TypeConverter
import io.github.tonnyl.moka.data.item.ProjectState

object ProjectStateConverters {

    @TypeConverter
    @JvmStatic
    fun projectStateToLong(state: ProjectState): Int = state.ordinal

    @TypeConverter
    @JvmStatic
    fun fromOrdinal(value: Int?): ProjectState = when (value) {
        ProjectState.OPEN.ordinal -> {
            ProjectState.OPEN
        }
        // including ProjectState.CLOSED.ordinal
        else -> {
            ProjectState.CLOSED
        }
    }

}