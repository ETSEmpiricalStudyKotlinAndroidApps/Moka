package io.github.tonnyl.moka.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import com.google.accompanist.insets.*
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.serializers.store.SettingSerializer
import io.github.tonnyl.moka.serializers.store.data.Theme
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@ExperimentalAnimatedInsets
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
            val windowInsetsController = LocalWindowInsetsController.current
            val isSystemDarkTheme = isSystemInDarkTheme()
            DisposableEffect(settings.theme) {
                windowInsetsController?.isAppearanceLightStatusBars =
                    !isSystemDarkTheme && settings.theme != Theme.DARK
                windowInsetsController?.isAppearanceLightNavigationBars =
                    isSystemDarkTheme && settings.theme != Theme.DARK
                onDispose { }
            }

            ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                val navScrimColor = MaterialTheme.colors.background.copy(alpha = .3f)
                val statusScrimColor = MaterialTheme.colors.background.copy(alpha = .9f)
                Box(modifier = Modifier.fillMaxSize()) {
                    Spacer(
                        modifier = Modifier
                            .statusBarsHeight()
                            .navigationBarsPadding(bottom = false)
                            .zIndex(zIndex = 999F)
                            .fillMaxWidth()
                            .background(color = statusScrimColor)
                            .align(alignment = Alignment.TopCenter)
                    )
                    Spacer(
                        modifier = Modifier
                            .navigationBarsWidth(side = HorizontalSide.Left)
                            .zIndex(zIndex = 999F)
                            .fillMaxHeight()
                            .background(color = navScrimColor)
                            .align(alignment = Alignment.CenterStart)
                    )
                    Spacer(
                        modifier = Modifier
                            .navigationBarsWidth(side = HorizontalSide.Right)
                            .zIndex(zIndex = 999F)
                            .fillMaxHeight()
                            .background(color = navScrimColor)
                            .align(alignment = Alignment.CenterEnd)
                    )
                    Spacer(
                        modifier = Modifier
                            .navigationBarsHeight()
                            .zIndex(zIndex = 999F)
                            .fillMaxWidth()
                            .background(color = navScrimColor)
                            .align(alignment = Alignment.BottomCenter)
                    )
                    content()
                }
            }
        }
    )
}