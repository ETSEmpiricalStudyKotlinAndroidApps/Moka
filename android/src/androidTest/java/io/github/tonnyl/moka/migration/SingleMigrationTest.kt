package io.github.tonnyl.moka.migration

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.tonnyl.moka.common.db.MIGRATION_1_2
import io.tonnyl.moka.common.db.MokaDataBase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SingleMigrationTest {

    private val testDbName = "single-migration-test"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MokaDataBase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        helper.createDatabase(testDbName, 1).apply {
            // do nothing because migrate-1-to-2 only creates tables, without dropping nor modifying other tables.

            close()
        }

        helper.runMigrationsAndValidate(testDbName, 2, true, MIGRATION_1_2)
    }

}