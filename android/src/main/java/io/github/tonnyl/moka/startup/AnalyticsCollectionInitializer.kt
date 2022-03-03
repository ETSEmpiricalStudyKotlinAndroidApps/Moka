package io.github.tonnyl.moka.startup

import android.content.Context
import androidx.startup.Initializer
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsCollectionInitializer: Initializer<Unit> {

    override fun create(context: Context) {
        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(true)
    }

    override fun dependencies() = emptyList<Class<out Initializer<*>>>()

}