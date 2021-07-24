package io.github.tonnyl.moka.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.ui.auth.AuthActivity
import kotlinx.serialization.ExperimentalSerializationApi

class SplashActivity : ComponentActivity() {

    @ExperimentalSerializationApi
    @ExperimentalAnimatedInsets
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        var haveSetListener = false
        (application as MokaApp).accountInstancesLiveData.observe(this) { accountInstances ->
            if (!haveSetListener) {
                haveSetListener = true

                splashScreen.setOnExitAnimationListener {
                    val clazz = if (accountInstances.isEmpty()) {
                        AuthActivity::class.java
                    } else {
                        MainActivity::class.java
                    }
                    startActivity(Intent(this@SplashActivity, clazz))

                    finish()

                    it.remove()
                }
            }
        }
    }

}