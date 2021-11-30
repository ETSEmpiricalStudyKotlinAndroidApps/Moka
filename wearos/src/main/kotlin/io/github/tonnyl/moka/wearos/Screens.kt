package io.github.tonnyl.moka.wearos

sealed class Screen(val route: String) {

    object Timeline : Screen("timeline")

}