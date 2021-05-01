package io.github.tonnyl.moka.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.ui.auth.AuthActivity
import io.github.tonnyl.moka.ui.theme.LocalWindowInsetsController
import io.github.tonnyl.moka.ui.theme.MokaTheme
import kotlinx.serialization.ExperimentalSerializationApi

class SplashActivity : ComponentActivity() {

    @ExperimentalSerializationApi
    @ExperimentalAnimatedInsets
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        with(window) {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            navigationBarColor = Color.TRANSPARENT
            statusBarColor = Color.TRANSPARENT
        }

        setContent {
            val windowInsetsControllerCompat =
                remember { WindowInsetsControllerCompat(window, window.decorView) }
            CompositionLocalProvider(LocalWindowInsetsController provides windowInsetsControllerCompat) {
                MokaTheme {
                    Surface {
                        SplashScreen()
                    }
                }
            }
        }

        (application as MokaApp).accountInstancesLiveData.observe(this) { accountInstances ->
            val clazz = if (accountInstances.isEmpty()) {
                AuthActivity::class.java
            } else {
                MainActivity::class.java
            }
            startActivity(Intent(this@SplashActivity, clazz))

            finish()
        }
    }

}