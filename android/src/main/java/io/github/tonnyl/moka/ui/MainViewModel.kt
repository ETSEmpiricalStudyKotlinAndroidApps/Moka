package io.github.tonnyl.moka.ui

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.util.readEmojisFromAssets
import io.tonnyl.moka.common.data.Emoji
import io.tonnyl.moka.common.data.EmojiCategory
import io.tonnyl.moka.common.data.SearchableEmoji
import io.tonnyl.moka.common.serialization.json
import io.tonnyl.moka.common.store.data.ExploreLanguage
import io.tonnyl.moka.common.store.data.SignedInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import okio.buffer
import okio.source

@ExperimentalPagingApi
@ExperimentalSerializationApi
class MainViewModel(
    app: Application
) : AndroidViewModel(app) {

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
            logcat(priority = LogPriority.ERROR) { e.asLog() }
        } finally {
            emit(emojis)
        }
    }

    val localLanguages: LiveData<List<ExploreLanguage>> = liveData(
        context = viewModelScope.coroutineContext + Dispatchers.IO
    ) {
        try {
            val result = app.assets.open("languages.json").use { inputStream ->
                val jsonString = inputStream.source().buffer().readString(Charsets.UTF_8)
                json.decodeFromString<List<ExploreLanguage>>(jsonString)
            }

            emit(result)
        } catch (e: Exception) {
            logcat(priority = LogPriority.ERROR) { e.asLog() }

            emit(emptyList<ExploreLanguage>())
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
                logcat(priority = LogPriority.ERROR) { e.asLog() }

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
                logcat(priority = LogPriority.ERROR) { e.asLog() }
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

    fun moveAccountToFirst(
        account: SignedInAccount,
        index: Int
    ) {
        viewModelScope.launch {
            try {
                getApplication<MokaApp>().accountsDataStore.updateData { store ->
                    val newAccounts = store.accounts.toMutableList()
                    newAccounts.removeAt(index = index)
                    newAccounts.add(0, account)
                    store.copy(accounts = newAccounts)
                }
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }
            }
        }
    }

}