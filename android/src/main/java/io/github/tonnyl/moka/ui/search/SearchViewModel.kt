package io.github.tonnyl.moka.ui.search

import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.github.tonnyl.moka.ui.search.repositories.SearchedRepositoriesItemDataSource
import io.github.tonnyl.moka.ui.search.users.SearchedUsersItemDataSource
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.data.SearchedUserOrOrgItem
import io.tonnyl.moka.common.store.data.Query
import io.tonnyl.moka.common.store.data.SearchHistory
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.graphql.fragment.RepositoryListItemFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

@ExperimentalSerializationApi
class SearchViewModel(
    private val accountInstance: AccountInstance,
    private val initialSearchKeyword: String
) : ViewModel() {

    private val _userInput = MutableLiveData<String>()
    val userInput: LiveData<String>
        get() = _userInput

    var usersFlow: Flow<PagingData<SearchedUserOrOrgItem>>? = null
        private set
    var repositoriesFlow: Flow<PagingData<RepositoryListItemFragment>>? = null
        private set

    val queryHistoryStore: DataStore<SearchHistory> = accountInstance.searchHistoryDataStore

    init {
        if (initialSearchKeyword.isNotEmpty()) {
            updateInput(initialSearchKeyword)
        }
    }

    fun updateInput(newInput: String) {
        if (userInput.value == newInput) {
            return
        }

        _userInput.value = newInput

        usersFlow = Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                SearchedUsersItemDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    query = newInput
                )
            }
        ).flow.cachedIn(viewModelScope)

        repositoriesFlow = Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                SearchedRepositoriesItemDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    query = newInput
                )
            }
        ).flow.cachedIn(viewModelScope)

        viewModelScope.launch {
            try {
                queryHistoryStore.updateData { history ->
                    val queries = history.queries.toMutableList()
                    queries.removeAll { it.keyword == newInput }
                    queries.add(
                        0,
                        Query(
                            keyword = newInput,
                            queryTime = Clock.System.now()
                        )
                    )

                    history.copy(
                        queries = if (queries.size >= MAX_SEARCH_HISTORY_SIZE) {
                            queries.subList(0, MAX_SEARCH_HISTORY_SIZE)
                        } else {
                            queries
                        }
                    )
                }
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) {
                    "add query failed: ${e.asLog()}"
                }
            }
        }
    }

    fun removeQuery(query: Query) {
        viewModelScope.launch {
            try {
                queryHistoryStore.updateData { history ->
                    history.copy(queries = history.queries.filter { it != query })
                }
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) {
                    "remove query failed: ${e.asLog()}"
                }
            }
        }
    }

    companion object {

        private const val MAX_SEARCH_HISTORY_SIZE = 3

    }

}