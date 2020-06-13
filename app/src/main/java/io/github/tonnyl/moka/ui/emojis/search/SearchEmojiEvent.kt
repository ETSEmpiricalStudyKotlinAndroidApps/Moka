package io.github.tonnyl.moka.ui.emojis.search

sealed class SearchEmojiEvent {

    class SelectEmoji(
        val emojiName: String
    ) : SearchEmojiEvent()

}