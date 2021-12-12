package io.tonnyl.moka.common.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.tonnyl.moka.common.BuildConfig
import io.tonnyl.moka.common.db.converter.*
import io.tonnyl.moka.common.db.dao.*
import io.tonnyl.moka.common.db.data.*

@Database(
    entities = [
        Event::class,
        Notification::class,
        TrendingDeveloper::class,
        TrendingRepository::class,
        Project::class,
        RemoteKeys::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(
    DateConverters::class,
    EventGollumPageListConverters::class,
    TrendingRepositoryBuiltByListConverters::class,
    EventGistFileMapConverters::class,
    NotificationReasonsConverters::class
)
abstract class MokaDataBase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    abstract fun notificationsDao(): NotificationDao

    abstract fun projectsDao(): ProjectsDao

    abstract fun trendingDevelopersDao(): TrendingDeveloperDao

    abstract fun trendingRepositoriesDao(): TrendingRepositoryDao

    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: MokaDataBase? = null

        private const val DATABASE_NAME_PREFIX = "Moka-db"

        private var currentUserId: Long = -1L

        /**
         * Get user-related room singleton.
         * Cause it is user-related, this function should not be called until user login.
         *
         * @param context Context.
         * @param userId User identification. See [io.github.tonnyl.moka.data.AuthenticatedUser.id].
         *
         * @return room instance.
         */
        fun getInstance(context: Context, userId: Long): MokaDataBase {
            if (currentUserId != userId) {
                currentUserId = userId

                return synchronized(this) {
                    buildDatabase(context, currentUserId).also {
                        instance = it
                    }
                }
            }

            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context, currentUserId).also {
                    instance = it
                }
            }
        }

        private fun buildDatabase(context: Context, userId: Long): MokaDataBase {
            return Room.databaseBuilder(
                context,
                MokaDataBase::class.java,
                DATABASE_NAME_PREFIX + userId
            ).addMigrations(
                MIGRATION_1_2
            ).apply {
                // If Room cannot find a migration path for upgrading an existing database
                // on a device to the current version, an `IllegalStateException` occurs.
                // Calling `fallbackToDestructiveMigration()` method tells room to destructively
                // recreate the tables in app's database when it needs to perform an incremental
                // migration where there is no defined migration path.
                // On production flavor, it's acceptable to lose existing data rather than crash when a migration fails.
                // On debug flavor, let the app crash to warn the developer that something is wrong.
                if (!BuildConfig.DEBUG) {
                    fallbackToDestructiveMigration()
                }
            }.build()
        }

    }

}