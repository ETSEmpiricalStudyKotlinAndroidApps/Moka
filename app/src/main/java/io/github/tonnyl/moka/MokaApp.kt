package io.github.tonnyl.moka

import android.app.Application
import com.apollographql.apollo.cache.normalized.sql.ApolloSqlHelper
import java.io.File

class MokaApp : Application() {

    companion object {
        // Directory where apollo cached responses will be stored
        lateinit var CACHE_FILE: File
        // Create the ApolloSqlHelper. Please note that if null is passed in as the name, you will get an in-memory SqlLite database that
        // will not persist across restarts of the app.
        lateinit var apolloSqlHelper: ApolloSqlHelper
        private const val APOLLO_CACHE_DB_NAME = "apollo_db_cache"
    }

    override fun onCreate() {
        super.onCreate()

        CACHE_FILE = File(cacheDir.toURI())

        apolloSqlHelper = ApolloSqlHelper.create(this, APOLLO_CACHE_DB_NAME)
    }

}