package io.github.tonnyl.moka.migration

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.tonnyl.moka.db.MIGRATION_1_2
import io.github.tonnyl.moka.db.MokaDataBase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AllMigrationsTest {

    private val testDbName = "all-migration-test"

    private val allMigrations = arrayOf(MIGRATION_1_2)

    @get:Rule
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MokaDataBase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create earliest version of the database.
        migrationTestHelper.createDatabase(testDbName, 1).apply {
            close()
        }

        // Open latest version of the database. Room will validate the schema
        // once all migrations execute.
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            MokaDataBase::class.java,
            testDbName
        ).addMigrations(*allMigrations).build().apply {
            openHelper.writableDatabase.close()
        }
    }

}