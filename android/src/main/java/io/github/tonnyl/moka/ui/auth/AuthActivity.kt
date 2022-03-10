package io.github.tonnyl.moka.ui.auth

import android.app.Application
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
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.MainActivity
import io.github.tonnyl.moka.ui.auth.AuthEvent.FinishAndGo
import io.github.tonnyl.moka.ui.theme.LocalWindowInsetsController
import io.github.tonnyl.moka.ui.theme.MokaTheme
import io.github.tonnyl.moka.widget.AppBarNavigationIcon
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar

class AuthActivity : ComponentActivity() {

    private val viewModel by viewModels<AuthViewModel>(
        extrasProducer = {
            MutableCreationExtras().apply {
                this[APPLICATION_KEY] = this@AuthActivity.applicationContext as Application
            }
        }
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
                                AppBarNavigationIcon(
                                    onClick = { finish() },
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = stringResource(id = R.string.navigate_close)
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