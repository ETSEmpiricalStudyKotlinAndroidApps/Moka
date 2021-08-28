package io.github.tonnyl.moka.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val LightThemeColors = lightColors(
    primary = Color(0xff6200ee),
    primaryVariant = Color(0xff3700b3),
    onPrimary = Color(0xffffffff),
    secondary = Color(0xff03dac6),
    secondaryVariant = Color(0xff018786),
    onSecondary = Color(0xff000000),
    background = Color(0xffffffff),
    onBackground = Color(0xff000000),
    surface = Color(0xffffffff),
    onSurface = Color(0xff000000),
    error = Color(0xffc51162),
    onError = Color(0xffffffff)
)

val DarkThemeColors = darkColors(
    primary = Color(0xfffce548),
    primaryVariant = Color(0xffc6b300),
    onPrimary = Color(0xff000000),
    secondary = Color(0xff407bfa),
    onSecondary = Color(0xff000000),
    background = Color(0xff000000),
    onBackground = Color(0xffffffff),
    surface = Color(0xff000000),
    onSurface = Color(0xffffffff),
    error = Color(0xffcf6679),
    onError = Color(0xff000000)
)

val issuePrGreen = Color(0xFF388E3C)

val userStatusDndYellow = Color(0xFFFCD230)

val commitModificationColor = Color(blue = 192, green = 92, red = 0)
val commitAdditionColor = Color(red = 45, green = 164, blue = 78)
val commitDeletionColor = Color(red = 209, green = 43, blue = 55)