package io.github.tonnyl.moka.ui

import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.*
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.mutations.addReaction
import io.github.tonnyl.moka.network.mutations.removeReaction
import io.github.tonnyl.moka.type.ReactionContent
import io.github.tonnyl.moka.ui.UserEvent.*
import io.github.tonnyl.moka.ui.explore.filters.LocalLanguage
import io.github.tonnyl.moka.util.MoshiInstance
import io.github.tonnyl.moka.util.readEmojisFromAssets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.nio.charset.Charset

class MainViewModel(
    app: MokaApp
) : AndroidViewModel(app) {

    val currentUser = MutableLiveData<AuthenticatedUser>()

    private val _event = MutableLiveData<Event<UserEvent>>()
    val event: LiveData<Event<UserEvent>>
        get() = _event

    private val _fragmentScopedEvent = MutableLiveData<Event<UserEvent>>()
    val fragmentScopedEvent: LiveData<Event<UserEvent>>
        get() = _fragmentScopedEvent

    private var allSearchableEmojis = mutableListOf<SearchableEmoji>()

    private val _searchableEmojis = MutableLiveData<List<SearchableEmoji>>()
    val searchableEmojis: LiveData<List<SearchableEmoji>>
        get() = _searchableEmojis

    val emojis = liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
        try {
            val result = app.readEmojisFromAssets()

            val emojis = mutableListOf<EmojiType>()

            // read recent emojis string from sp and convert it.
            val sp = PreferenceManager.getDefaultSharedPreferences(app.applicationContext)
            val recentEmojisString = sp.getString("recent_used_emojis", null)
            if (!recentEmojisString.isNullOrEmpty()) {
                val recentEmojis = MoshiInstance.emojiListAdapter
                    .fromJson(recentEmojisString) ?: emptyList<Emoji>()

                if (recentEmojis.isNotEmpty()) {
                    emojis.add(EmojiCategory.RecentlyUsed)
                    emojis.addAll(recentEmojis)
                }
            }

            emojis.add(EmojiCategory.SmileysAndEmotion)
            result.forEachIndexed { index, current ->
                emojis.add(current)

                val next = result.getOrNull(index + 1)
                if (next != null && next.category != current.category) {
                    emojis.add(
                        EmojiCategory.values().first {
                            it.categoryValue == next.category
                        }
                    )
                }
            }

            emit(emojis)
        } catch (e: Exception) {
            Timber.e(e)

            emit(emptyList<EmojiType>())
        }
    }

    val localLanguages: LiveData<List<LocalLanguage>> = liveData(
        context = viewModelScope.coroutineContext + Dispatchers.IO
    ) {
        try {
            val result = app.assets.open("languages.json").use { inputStream ->
                val buffer = ByteArray(inputStream.available())
                inputStream.read(buffer)
                val json = String(buffer, Charset.forName("UTF-8"))
                MoshiInstance.localLanguageListAdapter.fromJson(json)
            } ?: emptyList<LocalLanguage>()

            emit(result)
        } catch (e: Exception) {
            Timber.e(e)

            emit(emptyList<LocalLanguage>())
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = app.readEmojisFromAssets()

                result.forEach { e ->
                    e.names.forEach { name ->
                        allSearchableEmojis.add(
                            SearchableEmoji(e.emoji, name, e.category)
                        )
                    }
                }

                allSearchableEmojis.sortBy { it.name }

                _searchableEmojis.postValue(allSearchableEmojis)
            } catch (e: Exception) {
                Timber.e(e)

                _searchableEmojis.postValue(emptyList())
            }
        }
    }

    fun getUserProfile() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val response = runBlocking {
//                    GraphQLClient.apolloClient
//                        .query(
//                            ViewerQuery.builder()
//                                .build()
//                        )
//                        .execute()
//                }
//
//                Timber.d("get viewer info call success, resp = $response")
//
//                loginUserProfile.postValue(response.data())
//            } catch (e: Exception) {
//                Timber.e(e, "get viewer info call error: ${e.message}")
//
//            }
//        }
    }

    fun showSearch() {
        _event.value = Event(ShowSearch)
    }

    fun showAccounts() {
        _event.value = Event(ShowAccounts)
    }

    fun showReactionDialog(
        reactableId: String,
        currentReactionGroups: List<ReactionGroup>?
    ) {
        val userHasReactedContents = currentReactionGroups?.filter {
            it.viewerHasReacted
        }?.map {
            it.content.rawValue
        }?.toTypedArray()
        _event.value = Event(ShowReactionDialog(reactableId, userHasReactedContents))
    }

    fun react(
        content: ReactionContent,
        reactableId: String,
        isSelected: Boolean
    ) {
        _event.value = Event(DismissReactionDialog)

        viewModelScope.launch(Dispatchers.IO) {
            _fragmentScopedEvent.postValue(
                Event(
                    React(
                        Resource.loading(null),
                        content,
                        reactableId,
                        isSelected
                    )
                )
            )

            try {
                if (isSelected) {
                    addReaction(reactableId, content)
                } else {
                    removeReaction(reactableId, content)
                }

                _fragmentScopedEvent.postValue(
                    Event(
                        React(
                            Resource.success(null),
                            content,
                            reactableId,
                            isSelected
                        )
                    )
                )
            } catch (e: Exception) {
                Timber.e(e)

                _fragmentScopedEvent.postValue(
                    Event(
                        React(
                            Resource.error(e.message, null),
                            content,
                            reactableId,
                            isSelected
                        )
                    )
                )
            }
        }
    }

    fun filterSearchable(text: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (text.isNullOrEmpty()) {
                    if (emojis.value != allSearchableEmojis) {
                        _searchableEmojis.postValue(allSearchableEmojis)
                    }
                } else {
                    _searchableEmojis.postValue(
                        allSearchableEmojis.filter {
                            it.name.contains(text, ignoreCase = true)
                        }
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun getEmojiByName(name: String?): SearchableEmoji? {
        if (name.isNullOrEmpty()) {
            return null
        }

        return allSearchableEmojis.firstOrNull {
            it.name == name
        }
    }

    fun updateUserStatus(status: UserStatus?) {
        _fragmentScopedEvent.value = Event(UpdateUserState(status))
    }

}