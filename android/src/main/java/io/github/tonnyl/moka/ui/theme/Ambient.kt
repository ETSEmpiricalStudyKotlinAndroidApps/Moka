package io.github.tonnyl.moka.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import io.github.tonnyl.moka.ui.MainViewModel
import io.tonnyl.moka.common.AccountInstance

val LocalWindowInsetsController = staticCompositionLocalOf<WindowInsetsControllerCompat?> { null }

val LocalAccountInstance = staticCompositionLocalOf<AccountInstance?> { null }
val LocalNavController = staticCompositionLocalOf<NavHostController> { error("No NavController") }

val LocalMainViewModel = staticCompositionLocalOf<MainViewModel> { error("No MainViewModel") }