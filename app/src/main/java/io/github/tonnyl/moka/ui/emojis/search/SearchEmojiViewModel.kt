package io.github.tonnyl.moka.ui.emojis.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.tonnyl.moka.data.SearchableEmoji
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.UserEvent

class SearchEmojiViewModel : ViewModel() {

    private val _event = MutableLiveData<Event<UserEvent.SelectEmoji>>()
    val event: LiveData<Event<UserEvent.SelectEmoji>>
        get() = _event

    fun selectEmoji(searchableEmoji: SearchableEmoji) {
        _event.value = Event(UserEvent.SelectEmoji(searchableEmoji.name))
    }

}