package io.github.tonnyl.moka.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.github.tonnyl.moka.MokaApp
import io.tonnyl.moka.common.store.SettingSerializer
import io.tonnyl.moka.common.store.data.Theme

@Composable
fun MokaTheme(content: @Composable () -> Unit) {
    val settings by (LocalContext.current.applicationContext as MokaApp).settingsDataStore
        .data
        .collectAsState(initial = SettingSerializer.defaultValue)
    MaterialTheme(
        shapes = MokaShapes,
        colors = when (settings.theme) {
            Theme.LIGHT -> {
                LightThemeColors
            }
            Theme.DARK -> {
                DarkThemeColors
            }
            Theme.AUTO -> {
                if (isSystemInDarkTheme()) {
                    DarkThemeColors
                } else {
                    LightThemeColors
                }
            }
        },
        content = {
            ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                val gestureInsets = LocalWindowInsets.current.navigationBars
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colors.isLight
                val navigationScrimColor = MaterialTheme.colors.background.copy(alpha = .9f)

                // https://medium.com/androiddevelopers/gesture-navigation-handling-visual-overlaps-4aed565c134c
                // Hardcoding a height value is not a good idea. But no better idea was found.
                val defaultNavigationBarHeight = with(LocalDensity.current) {
                    48.dp.toPx()
                }
                val gestureNavigationEnabled = !(gestureInsets.bottom >= defaultNavigationBarHeight
                        || gestureInsets.left >= defaultNavigationBarHeight
                        || gestureInsets.right >= defaultNavigationBarHeight)

                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )

                    systemUiController.setNavigationBarColor(
                        color = if (gestureNavigationEnabled) {
                            Color.Transparent
                        } else {
                            navigationScrimColor
                        },
                        darkIcons = useDarkIcons,
                        navigationBarContrastEnforced = gestureNavigationEnabled,
                        transformColorForLightContent = { navigationScrimColor }
                    )
                }

                content()
            }
        }
    )
}

@Composable
fun MediaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        shapes = MokaShapes,
        colors = DarkThemeColors,
        content = {
            ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                val gestureInsets = LocalWindowInsets.current.navigationBars
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colors.isLight
                val navigationScrimColor = MaterialTheme.colors.background.copy(alpha = .9f)

                // https://medium.com/androiddevelopers/gesture-navigation-handling-visual-overlaps-4aed565c134c
                // Hardcoding a height value is not a good idea. But no better idea was found.
                val defaultNavigationBarHeight = with(LocalDensity.current) {
                    48.dp.toPx()
                }
                val gestureNavigationEnabled = !(gestureInsets.bottom >= defaultNavigationBarHeight
                        || gestureInsets.left >= defaultNavigationBarHeight
                        || gestureInsets.right >= defaultNavigationBarHeight)

                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )

                    systemUiController.setNavigationBarColor(
                        color = if (gestureNavigationEnabled) {
                            Color.Transparent
                        } else {
                            navigationScrimColor
                        },
                        darkIcons = useDarkIcons,
                        navigationBarContrastEnforced = gestureNavigationEnabled,
                        transformColorForLightContent = { navigationScrimColor }
                    )
                }

                content()
            }
        }
    )
}