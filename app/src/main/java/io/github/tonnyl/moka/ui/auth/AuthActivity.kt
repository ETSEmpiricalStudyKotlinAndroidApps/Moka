package io.github.tonnyl.moka.ui.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.MainActivity
import io.github.tonnyl.moka.ui.auth.AuthEvent.FinishAndGo
import io.github.tonnyl.moka.ui.theme.LocalWindowInsetsController
import io.github.tonnyl.moka.ui.theme.MokaTheme
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar

class AuthActivity : ComponentActivity() {

    private val viewModel by viewModels<AuthViewModel>(factoryProducer = {
        ViewModelFactory(this)
    })

    @ExperimentalAnimatedInsets
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        with(window) {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            navigationBarColor = Color.TRANSPARENT
            statusBarColor = Color.TRANSPARENT
        }

        var argsHaveBeenHandled = false

        setContent {
            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState()

            val windowInsetsControllerCompat =
                remember { WindowInsetsControllerCompat(window, window.decorView) }
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
                            NavHost(
                                navController = navController,
                                startDestination = Screen.Auth.route
                            ) {
                                composable(
                                    route = Screen.Auth.route,
                                    arguments = listOf(
                                        navArgument("code") {
                                            nullable = true
                                            type = NavType.StringType
                                            defaultValue = null
                                        },
                                        navArgument("state") {
                                            nullable = true
                                            type = NavType.StringType
                                            defaultValue = null
                                        }
                                    ),
                                    deepLinks = listOf(
                                        navDeepLink {
                                            uriPattern =
                                                "https://tonnyl.io/moka/callback?code={code}&state={state}"
                                        }
                                    )
                                ) { backStackEntry ->
                                    val codeArg = backStackEntry.arguments?.getString("code")
                                    val stateArg = backStackEntry.arguments?.getString("state")

                                    if (!argsHaveBeenHandled
                                        && !codeArg.isNullOrEmpty()
                                        && !stateArg.isNullOrEmpty()
                                    ) {
                                        viewModel.getAccessToken(codeArg, stateArg)

                                        argsHaveBeenHandled = true
                                    }

                                    val authTokenAndUserResource by viewModel.authTokenAndUserResult.observeAsState()
                                    AuthScreen(
                                        authTokenAndUserResource = authTokenAndUserResource,
                                        scaffoldState = scaffoldState
                                    )
                                }
                            }
                        }

                        InsetAwareTopAppBar(
                            title = { Text("") },
                            navigationIcon = {
                                IconButton(
                                    onClick = { finish() },
                                    content = {
                                        Icon(
                                            contentDescription = stringResource(id = R.string.navigate_up),
                                            painter = painterResource(id = R.drawable.ic_close_24)
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

sealed class Screen(val route: String) {

    object Auth : Screen("auth?code={code}&state={state}")

}