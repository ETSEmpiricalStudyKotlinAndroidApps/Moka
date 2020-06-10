package io.github.tonnyl.moka.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.db.converter.*
import io.github.tonnyl.moka.db.dao.*

@Database(
    entities = [
        Event::class,
        Notification::class,
        TrendingDeveloper::class,
        TrendingRepository::class,
        Project::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    DateConverters::class,
    ProjectStateConverters::class,
    UriConverters::class,
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
            ).build()
        }

    }

}