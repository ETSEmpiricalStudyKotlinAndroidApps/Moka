package io.github.tonnyl.moka

import android.app.Application
import timber.log.Timber

class MokaApp : Application() {

    companion object {
        const val PER_PAGE = 16
        const val MAX_SIZE_OF_PAGED_LIST = 1024
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}