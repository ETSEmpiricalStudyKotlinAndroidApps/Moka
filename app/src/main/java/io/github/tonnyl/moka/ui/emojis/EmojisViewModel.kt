package io.github.tonnyl.moka.ui.emojis

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.tonnyl.moka.data.Emoji
import io.github.tonnyl.moka.data.EmojiCategory
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.emojis.EmojiEvent.EmojiSelected
import io.github.tonnyl.moka.ui.emojis.EmojiEvent.ScrollToPosition

class EmojisViewModel : ViewModel() {

    private val _event = MutableLiveData<Event<EmojiEvent>>()
    val event: LiveData<Event<EmojiEvent>>
        get() = _event

    @MainThread
    fun scrollToCategoryStart(category: EmojiCategory) {
        _event.value = Event(ScrollToPosition(category))
    }

    @MainThread
    fun selectEmoji(emoji: Emoji) {
        _event.value = Event(EmojiSelected(emoji.names.first()))
    }

}