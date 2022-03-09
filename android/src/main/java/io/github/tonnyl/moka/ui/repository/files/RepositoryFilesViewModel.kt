package io.github.tonnyl.moka.ui.repository.files

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.data.TreeEntryType
import io.tonnyl.moka.common.data.treeEntryType
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.graphql.CurrentLevelTreeViewQuery
import io.tonnyl.moka.graphql.fragment.TreeEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

data class RepositoryFilesViewModelExtra(
    val accountInstance: AccountInstance,
    val login: String,
    val repositoryName: String,
    val expression: String
)

class RepositoryFilesViewModel(private val extra: RepositoryFilesViewModelExtra) : ViewModel() {

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
                val response = extra.accountInstance.apolloGraphQLClient
                    .apolloClient
                    .query(
                        query = CurrentLevelTreeViewQuery(
                            login = extra.login,
                            repoName = extra.repositoryName,
                            expression = extra.expression
                        )
                    )

                val entries = response.execute().data
                    ?.repository
                    ?.`object`
                    ?.tree
                    ?.entries
                    ?.map {
                        it.treeEntry
                    }

                _entry.postValue(Resource.success(data = entries?.sortedBy { it.treeEntryType == TreeEntryType.BLOB }))
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _entry.postValue(Resource.error(exception = e, data = entry.value?.data))
            }
        }
    }

}