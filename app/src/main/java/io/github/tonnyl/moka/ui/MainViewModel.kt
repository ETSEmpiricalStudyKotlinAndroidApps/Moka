package io.github.tonnyl.moka.ui

import androidx.lifecycle.*
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.data.Emoji
import io.github.tonnyl.moka.data.EmojiCategory
import io.github.tonnyl.moka.data.SearchableEmoji
import io.github.tonnyl.moka.ui.explore.LocalLanguage
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

    private var allSearchableEmojis = mutableListOf<SearchableEmoji>()

    private val _searchableEmojis = MutableLiveData<List<SearchableEmoji>>()
    val searchableEmojis: LiveData<List<SearchableEmoji>>
        get() = _searchableEmojis

    val emojis = liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
        val emojis = mutableListOf<Pair<EmojiCategory, MutableList<Emoji>>>()
        try {
            val result = app.readEmojisFromAssets()
            result.forEach { current ->
                if (current.category != emojis.lastOrNull()?.first?.categoryValue) {
                    val category = EmojiCategory.values().firstOrNull {
                        it.categoryValue == current.category
                    }
                    if (category != null) {
                        emojis.add(Pair(category, mutableListOf()))
                    }
                }

                emojis.lastOrNull()?.let { pair ->
                    if (current.category == pair.first.categoryValue) {
                        pair.second.add(current)
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            emit(emojis)
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
            } ?: emptyList()

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

}