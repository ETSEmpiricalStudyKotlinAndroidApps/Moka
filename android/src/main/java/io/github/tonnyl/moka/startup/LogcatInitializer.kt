package io.github.tonnyl.moka.startup

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import logcat.AndroidLogcatLogger
import logcat.LogPriority

class LogcatInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        AndroidLogcatLogger.installOnDebuggableApp(
            application = context.applicationContext as Application,
            minPriority = LogPriority.VERBOSE
        )
    }

    override fun dependencies() = emptyList<Class<out Initializer<*>>>()

}