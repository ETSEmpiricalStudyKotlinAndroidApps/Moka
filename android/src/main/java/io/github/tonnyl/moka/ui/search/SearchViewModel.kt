package io.github.tonnyl.moka.ui.search

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
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.graphql.fragment.RepositoryListItemFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi

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
    }

}