package io.github.tonnyl.moka.startup

import android.content.Context
import androidx.startup.Initializer
import com.google.firebase.crashlytics.FirebaseCrashlytics

class CrashlyticsCollectionInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }

    override fun dependencies() = emptyList<Class<out Initializer<*>>>()

}