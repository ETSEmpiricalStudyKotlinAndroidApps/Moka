package io.github.tonnyl.moka.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.MainActivity
import io.github.tonnyl.moka.ui.auth.AuthEvent.FinishAndGo
import io.github.tonnyl.moka.ui.theme.MokaTheme
import io.github.tonnyl.moka.util.isDarkModeOn
import io.github.tonnyl.moka.util.updateForTheme

class AuthActivity : AppCompatActivity() {

    private val viewModel by viewModels<AuthViewModel>(factoryProducer = {
        ViewModelFactory(this)
    })

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateForTheme()

        // This app draws behind the system bars, so we want to handle fitting system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        var argsHaveBeenHandled = false

        setContent {
            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState()

            MokaTheme(darkTheme = resources.isDarkModeOn) {
                ProvideWindowInsets {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                backgroundColor = MaterialTheme.colors.background,
                                title = { Text("") },
                                navigationIcon = {
                                    IconButton(
                                        onClick = { finish() },
                                        icon = {
                                            Icon(asset = vectorResource(R.drawable.ic_close_24))
                                        }
                                    )
                                },
                                elevation = 0.dp,
                            )
                        },
                        snackbarHost = {
                            SnackbarHost(hostState = it) { data: SnackbarData ->
                                Snackbar(snackbarData = data)
                            }
                        },
                        scaffoldState = scaffoldState,
                        modifier = Modifier.fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                    ) {
                        NavHost(navController, startDestination = Screen.Auth.route) {
                            composable(
                                Screen.Auth.route,
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