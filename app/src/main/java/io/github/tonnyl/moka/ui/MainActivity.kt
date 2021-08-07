package io.github.tonnyl.moka.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import androidx.paging.ExperimentalPagingApi
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.pager.ExperimentalPagerApi
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.ui.auth.AuthActivity
import io.github.tonnyl.moka.ui.theme.*
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel> {
        MainViewModelFactory(applicationContext as MokaApp)
    }

    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalAnimatedInsets
    @ExperimentalComposeUiApi
    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    @ExperimentalPagingApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val windowInsetsControllerCompat =
                remember { WindowInsetsControllerCompat(window, window.decorView) }

            val accounts by viewModel.getApplication<MokaApp>().accountInstancesLiveData.observeAsState(
                initial = emptyList()
            )

            if (accounts.isNullOrEmpty()) {
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
                        MainScreen()
                    }
                }
            }
        }

        (application as MokaApp).accountInstancesLiveData.observe(this) { accountInstances ->
            if (accountInstances.isEmpty()) {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        }
    }

}