package io.github.tonnyl.moka.ui

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.compose.rememberNavController
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.ui.auth.AuthActivity
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.work.ContributionCalendarWorker

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>(
        extrasProducer = {
            MutableCreationExtras().apply {
                this[APPLICATION_KEY] = this@MainActivity.applicationContext as Application
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val windowInsetsControllerCompat =
                remember { ViewCompat.getWindowInsetsController(window.decorView) }

            val accounts by viewModel.getApplication<MokaApp>().accountInstancesLiveData.observeAsState(initial = emptyList())

            if (accounts.isEmpty()) {
                return@setContent
            }

            val navController = rememberNavController()

            val currentSignedInAccount = remember(key1 = accounts.first()) { accounts.first() }
            CompositionLocalProvider(
                LocalWindowInsetsController provides windowInsetsControllerCompat,
                LocalAccountInstance provides currentSignedInAccount,
                LocalNavController provides navController,
                LocalMainViewModel provides viewModel
            ) {
                MokaTheme {
                    Surface {
                        MainScreen(
                            startDestination = when (intent.action) {
                                ACTION_SEARCH -> {
                                    Screen.Search
                                }
                                ACTION_EXPLORE -> {
                                    Screen.Explore
                                }
                                ACTION_INBOX -> {
                                    Screen.Inbox
                                }
                                else -> {
                                    Screen.Timeline
                                }
                            }
                        )
                    }
                }
            }
        }

        val app = application as MokaApp
        app.accountInstancesLiveData.observe(this) { accountInstances ->
            if (accountInstances.isEmpty()) {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()

                return@observe
            }

            ContributionCalendarWorker.startOrCancelWorker(this)
            app.triggerNotificationWorker()
            viewModel.getUserProfile(accountInstances.first())
        }
    }

    companion object {

        /**
         * @see [@xml/shortcuts]
         */
        private const val ACTION_SEARCH = "io.github.tonnyl.moka.ACTION_SEARCH"
        private const val ACTION_EXPLORE = "io.github.tonnyl.moka.ACTION_EXPLORE"
        const val ACTION_INBOX = "io.github.tonnyl.moka.ACTION_INBOX"

    }

}