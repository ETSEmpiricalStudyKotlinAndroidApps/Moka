package io.tonnyl.moka.common.db

import androidx.annotation.VisibleForTesting
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@VisibleForTesting
val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // Create new tables
        database.execSQL("CREATE TABLE IF NOT EXISTS remote_keys (id TEXT PRIMARY KEY NOT NULL, prev TEXT, next TEXT)")
    }

}