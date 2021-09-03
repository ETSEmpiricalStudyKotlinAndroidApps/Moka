package io.github.tonnyl.moka.ui.repository.files

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.data.TreeEntryType
import io.github.tonnyl.moka.data.treeEntryType
import io.github.tonnyl.moka.fragment.TreeEntry
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.queries.CurrentLevelTreeViewQuery
import io.github.tonnyl.moka.queries.CurrentLevelTreeViewQuery.Data.Repository.Object.Companion.tree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber

@ExperimentalSerializationApi
class RepositoryFilesViewModel(
    private val accountInstance: AccountInstance,
    private val login: String,
    private val repositoryName: String,
    private val expression: String
) : ViewModel() {

    private val _entry = MutableLiveData<Resource<List<TreeEntry>>>()
    val entry: LiveData<Resource<List<TreeEntry>>>
        get() = _entry

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _entry.postValue(Resource.loading(data = entry.value?.data))

            try {
                val response = accountInstance.apolloGraphQLClient
                    .apolloClient
                    .query(
                        query = CurrentLevelTreeViewQuery(
                            login = login,
                            repoName = repositoryName,
                            expression = expression
                        )
                    )

                val entries = response.data
                    ?.repository
                    ?.object_
                    ?.tree()
                    ?.entries

                _entry.postValue(Resource.success(data = entries?.sortedBy { it.treeEntryType == TreeEntryType.BLOB }))
            } catch (e: Exception) {
                Timber.e(e)

                _entry.postValue(Resource.error(msg = e.message, data = entry.value?.data))
            }
        }
    }

}