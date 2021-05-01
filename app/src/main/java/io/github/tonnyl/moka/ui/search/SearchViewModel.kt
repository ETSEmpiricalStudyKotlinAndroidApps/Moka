package io.github.tonnyl.moka.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.ui.search.repositories.SearchedRepositoriesItemDataSource
import io.github.tonnyl.moka.ui.search.users.SearchedUsersItemDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class SearchViewModel(
    private val accountInstance: AccountInstance
) : ViewModel() {

    private val _userInput = MutableLiveData<String>()
    val userInput: LiveData<String>
        get() = _userInput

    var usersFlow: Flow<PagingData<SearchedUserOrOrgItem>>? = null
        private set
    var repositoriesFlow: Flow<PagingData<RepositoryItem>>? = null
        private set

    fun updateInput(newInput: String) {
        if (userInput.value == newInput) {
            return
        }

        _userInput.value = newInput

        usersFlow = Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                SearchedUsersItemDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    query = newInput
                )
            }
        ).flow.cachedIn(viewModelScope)

        repositoriesFlow = Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                SearchedRepositoriesItemDataSource(
                    apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                    query = newInput
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}