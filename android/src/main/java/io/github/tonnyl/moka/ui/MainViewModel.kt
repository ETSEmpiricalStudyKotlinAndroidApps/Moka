package io.github.tonnyl.moka.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.util.readEmojisFromAssets
import io.github.tonnyl.moka.util.toAccount
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.data.*
import io.tonnyl.moka.common.serialization.json
import io.tonnyl.moka.common.store.data.ExploreLanguage
import io.tonnyl.moka.common.store.data.ExploreSpokenLanguage
import io.tonnyl.moka.graphql.ViewerQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import okio.buffer
import okio.source

class MainViewModel(
    app: Application
) : AndroidViewModel(app) {

    private var allSearchableEmojis = mutableListOf<SearchableEmoji>()
    private var allExploreProgrammingLanguages = listOf<ExploreLanguage>()
    private var allExploreSpokenLanguages = listOf<ExploreSpokenLanguage>()

    private val _searchableEmojis = MutableLiveData<List<SearchableEmoji>>()
    val searchableEmojis: LiveData<List<SearchableEmoji>>
        get() = _searchableEmojis

    private val _programmingLanguages = MutableLiveData<List<ExploreLanguage>>()
    val programmingLanguages: LiveData<List<ExploreLanguage>>
        get() = _programmingLanguages

    private val _spokenLanguages = MutableLiveData<List<ExploreSpokenLanguage>>()
    val spokenLanguages: LiveData<List<ExploreSpokenLanguage>>
        get() = _spokenLanguages

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

                allExploreProgrammingLanguages = readListFromAssetAsLiveData(
                    context = app,
                    assetName = "languages.json"
                )

                allExploreSpokenLanguages = readListFromAssetAsLiveData(
                    context = app,
                    assetName = "spoken-languages.json"
                )

                _programmingLanguages.postValue(allExploreProgrammingLanguages)
                _spokenLanguages.postValue(allExploreSpokenLanguages)
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _searchableEmojis.postValue(emptyList())
            }
        }
    }

    fun getUserProfile(account: AccountInstance) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accountFromRemote = account.apolloGraphQLClient
                    .apolloClient.query(
                        query = ViewerQuery()
                    )
                    .execute()
                    .data
                    ?.viewer
                    ?.toAccount(existing = account.signedInAccount.account)

                if (accountFromRemote != null
                    && accountFromRemote != account.signedInAccount.account
                ) {
                    getApplication<MokaApp>().accountsDataStore.updateData { signedInAccounts ->
                        val newSignedInAccounts = signedInAccounts.accounts.toMutableList()
                        val index = newSignedInAccounts.indexOfFirst {
                            it.account.id == account.signedInAccount.account.id
                        }

                        if (index >= 0) {
                            newSignedInAccounts.removeAt(index)
                            newSignedInAccounts.add(
                                index,
                                SignedInAccount(
                                    accessToken = account.signedInAccount.accessToken,
                                    account = accountFromRemote
                                )
                            )
                        }

                        SignedInAccounts(accounts = newSignedInAccounts)
                    }
                }
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }
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

    fun deleteAccount(account: SignedInAccount) {
        viewModelScope.launch {
            try {
                val app = getApplication<MokaApp>()
                app.accountsDataStore.updateData { store ->
                    store.copy(accounts = store.accounts.filter { it != account })
                }
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }
            }
        }
    }

    fun filterProgrammingLanguages(text: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (text.isNullOrEmpty()) {
                    if (_programmingLanguages.value != allExploreProgrammingLanguages) {
                        _programmingLanguages.postValue(allExploreProgrammingLanguages)
                    }
                } else {
                    _programmingLanguages.postValue(
                        allExploreProgrammingLanguages.filter {
                            it.name.contains(text, ignoreCase = true)
                        }
                    )
                }
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }
            }
        }
    }

    fun filterSpokenLanguages(text: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (text.isNullOrEmpty()) {
                    if (_spokenLanguages.value != allExploreSpokenLanguages) {
                        _spokenLanguages.postValue(allExploreSpokenLanguages)
                    }
                } else {
                    _spokenLanguages.postValue(
                        allExploreSpokenLanguages.filter {
                            it.name.contains(text, ignoreCase = true)
                        }
                    )
                }
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }
            }
        }
    }

    private inline fun <reified T> readListFromAssetAsLiveData(
        context: Context,
        assetName: String
    ): List<T> {
        return context.assets.open(assetName).use { inputStream ->
            val jsonString = inputStream.source().buffer().readString(Charsets.UTF_8)
            json.decodeFromString(jsonString)
        }
    }

}