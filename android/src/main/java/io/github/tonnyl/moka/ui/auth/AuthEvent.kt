package io.github.tonnyl.moka.ui.auth

sealed class AuthEvent {

    object FinishAndGo : AuthEvent()

}