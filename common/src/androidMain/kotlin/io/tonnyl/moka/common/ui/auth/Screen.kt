package io.tonnyl.moka.common.ui.auth

sealed class Screen(val route: String) {

    object Auth : Screen("auth?code={code}&state={state}")

}