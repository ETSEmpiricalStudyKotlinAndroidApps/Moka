package io.github.tonnyl.moka.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.paging.ExperimentalPagingApi
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.ui.auth.AuthActivity
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalWindowInsetsController
import io.github.tonnyl.moka.ui.theme.MokaTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel> {
        MainViewModelFactory(applicationContext as MokaApp)
    }

    @ExperimentalAnimatedInsets
    @ExperimentalComposeUiApi
    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    @ExperimentalPagingApi
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

            val accounts by viewModel.getApplication<MokaApp>().accountInstancesLiveData.observeAsState(
                initial = emptyList()
            )

            if (accounts.isNullOrEmpty()) {
                return@setContent
            }

            val currentSignedInAccount = remember(key1 = accounts.first()) { accounts.first() }
            CompositionLocalProvider(
                LocalWindowInsetsController provides windowInsetsControllerCompat,
                LocalAccountInstance provides currentSignedInAccount
            ) {
                MokaTheme {
                    Surface {
                        MainScreen(mainViewModel = viewModel)
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