package io.github.tonnyl.moka.ui.profile.status

sealed class EditStatusEvent {

    object ShowClearStatusMenu : EditStatusEvent()

    object ShowEmojis : EditStatusEvent()

}