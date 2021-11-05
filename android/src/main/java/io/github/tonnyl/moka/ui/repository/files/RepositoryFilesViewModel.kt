package io.github.tonnyl.moka.ui.repository.files

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.data.TreeEntryType
import io.github.tonnyl.moka.data.treeEntryType
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.graphql.CurrentLevelTreeViewQuery
import io.tonnyl.moka.graphql.fragment.TreeEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

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

                val entries = response.execute().data
                    ?.repository
                    ?.object_
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