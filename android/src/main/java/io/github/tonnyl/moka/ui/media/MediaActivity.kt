package io.github.tonnyl.moka.ui.media

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.paging.ExperimentalPagingApi
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalMainViewModel
import io.github.tonnyl.moka.ui.theme.LocalWindowInsetsController
import io.github.tonnyl.moka.ui.theme.MediaTheme
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalAnimationApi
@ExperimentalSerializationApi
@ExperimentalPagingApi
class MediaActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>(
        extrasProducer = {
            MutableCreationExtras().apply {
                this[APPLICATION_KEY] = this@MediaActivity.applicationContext as Application
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = requireNotNull(intent.extras?.getString(ARG_URL))
        val filename = requireNotNull(intent.extras?.getString(ARG_FILENAME))
        val mediaType = MediaType.valueOf(requireNotNull(intent.extras?.getString(ARG_MEDIA_TYPE)))

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
                            filename = filename,
                            mediaType = mediaType
                        )
                    }
                }
            }

        }
    }

    companion object {

        const val ARG_URL = "arg_url"
        const val ARG_FILENAME = "arg_filename"
        const val ARG_MEDIA_TYPE = "arg_media_type"

    }

}