package io.github.tonnyl.moka.ui.media

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.paging.ExperimentalPagingApi
import coil.annotation.ExperimentalCoilApi
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.ViewModelFactory
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalMainViewModel
import io.github.tonnyl.moka.ui.theme.LocalWindowInsetsController
import io.github.tonnyl.moka.ui.theme.MediaTheme
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalCoilApi
@ExperimentalSerializationApi
@ExperimentalPagingApi
@ExperimentalMaterialApi
class MediaActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = requireNotNull(intent.extras?.getString(ARG_URL))
        val filename = requireNotNull(intent.extras?.getString(ARG_FILENAME))

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val windowInsetsControllerCompat =
                remember { ViewCompat.getWindowInsetsController(window.decorView) }

            val accounts by viewModel.getApplication<MokaApp>().accountInstancesLiveData.observeAsState(
                initial = emptyList()
            )

            if (accounts.isEmpty()) {
                return@setContent
            }

            val currentSignedInAccount = remember(key1 = accounts.first()) { accounts.first() }
            CompositionLocalProvider(
                LocalWindowInsetsController provides windowInsetsControllerCompat,
                LocalAccountInstance provides currentSignedInAccount,
                LocalMainViewModel provides viewModel
            ) {
                MediaTheme {
                    Surface {
                        MediaScreen(
                            activity = this,
                            url = url,
                            filename = filename
                        )
                    }
                }
            }

        }
    }

    companion object {

        const val ARG_URL = "arg_url"
        const val ARG_FILENAME = "arg_filename"

    }

}