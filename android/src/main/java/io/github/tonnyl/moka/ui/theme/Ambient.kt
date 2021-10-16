package io.github.tonnyl.moka.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.ui.MainViewModel
import kotlinx.serialization.ExperimentalSerializationApi

val LocalWindowInsetsController = staticCompositionLocalOf<WindowInsetsControllerCompat?> { null }

@ExperimentalSerializationApi
val LocalAccountInstance = staticCompositionLocalOf<AccountInstance?> { null }
val LocalNavController = staticCompositionLocalOf<NavHostController> { error("No NavController") }

@ExperimentalSerializationApi
val LocalMainViewModel = staticCompositionLocalOf<MainViewModel> { error("No MainViewModel") }