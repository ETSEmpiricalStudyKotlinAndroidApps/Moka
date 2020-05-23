package io.github.tonnyl.moka.ui.emojis

import io.github.tonnyl.moka.data.EmojiCategory

sealed class EmojiEvent {

    data class ScrollToPosition(val category: EmojiCategory) : EmojiEvent()

    data class EmojiSelected(val emojiName: String) : EmojiEvent()

}