package io.tonnyl.moka.common.ui.auth

sealed class AuthEvent {

    object FinishAndGo : AuthEvent()

}