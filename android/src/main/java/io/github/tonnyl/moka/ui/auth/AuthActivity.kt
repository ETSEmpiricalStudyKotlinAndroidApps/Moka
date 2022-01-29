package io.github.tonnyl.moka.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.paging.ExperimentalPagingApi
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.MainActivity
import io.github.tonnyl.moka.ui.ViewModelFactory
import io.github.tonnyl.moka.ui.theme.LocalWindowInsetsController
import io.github.tonnyl.moka.ui.theme.MokaTheme
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.tonnyl.moka.common.ui.auth.AuthEvent.FinishAndGo
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
class AuthActivity : ComponentActivity() {

    @OptIn(ExperimentalSerializationApi::class)
    private val viewModel by viewModels<AuthViewModel>(
        factoryProducer = {
            ViewModelFactory()
        }
    )

    @OptIn(
        ExperimentalSerializationApi::class,
        ExperimentalAnimatedInsets::class,
        ExperimentalMaterialApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val scaffoldState = rememberScaffoldState()

            val windowInsetsControllerCompat =
                remember { ViewCompat.getWindowInsetsController(window.decorView) }
            CompositionLocalProvider(LocalWindowInsetsController provides windowInsetsControllerCompat) {
                MokaTheme {
                    Surface {
                        Scaffold(
                            snackbarHost = {
                                SnackbarHost(hostState = it) { data: SnackbarData ->
                                    Snackbar(snackbarData = data)
                                }
                            },
                            scaffoldState = scaffoldState,
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                                .navigationBarsPadding()
                        ) {
                            val authTokenAndUserResource by viewModel.authTokenAndUserResult.observeAsState()
                            AuthScreen(
                                authTokenAndUserResource = authTokenAndUserResource,
                                scaffoldState = scaffoldState,
                                getAuthToken = viewModel::getAccessToken
                            )
                        }

                        InsetAwareTopAppBar(
                            title = { Text("") },
                            navigationIcon = {
                                IconButton(
                                    onClick = { finish() },
                                    content = {
                                        Icon(
                                            contentDescription = stringResource(id = R.string.navigate_up),
                                            imageVector = Icons.Outlined.Close
                                        )
                                    }
                                )
                            },
                            elevation = 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }

        viewModel.event.observe(this, EventObserver { event ->
            when (event) {
                FinishAndGo -> {
                    val intent = Intent(this@AuthActivity, MainActivity::class.java)
                    startActivity(intent)

                    finish()
                }
            }
        })

    }

}