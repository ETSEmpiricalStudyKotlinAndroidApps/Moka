package io.github.tonnyl.moka.ui.issues.create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.graphql.CreateIssueMutation
import io.tonnyl.moka.graphql.type.CreateIssueInput
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

@ExperimentalSerializationApi
data class CreateIssueViewModelExtra(
    val accountInstance: AccountInstance,
    val repoId: String,
    val defaultComment: String?
)

@ExperimentalSerializationApi
class CreateIssueViewModel(private val extra: CreateIssueViewModelExtra) : ViewModel() {

    private val _createIssueLiveData = MutableLiveData<Resource<CreateIssueMutation.Issue>?>()
    val createIssueLiveData: LiveData<Resource<CreateIssueMutation.Issue>?>
        get() = _createIssueLiveData

    val titleState = mutableStateOf(value = "")
    val bodyState = mutableStateOf(value = extra.defaultComment.orEmpty())

    fun create() {
        val title = titleState.value
        if (createIssueLiveData.value?.status == Status.LOADING
            || title.isEmpty()
        ) {
            return
        }

        viewModelScope.launch {
            try {
                _createIssueLiveData.value = Resource.loading(data = null)

                val issue = extra.accountInstance.apolloGraphQLClient.apolloClient
                    .mutation(
                        mutation = CreateIssueMutation(
                            input = CreateIssueInput(
                                repositoryId = extra.repoId,
                                title = title,
                                body = Optional.presentIfNotNull(value = bodyState.value.ifEmpty { null })
                            )
                        )
                    )
                    .execute().data?.createIssue?.issue

                _createIssueLiveData.value = Resource.success(data = issue)
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) {
                    e.asLog()
                }
                _createIssueLiveData.value = Resource.error(exception = e, data = null)
            }
        }
    }

    fun onCreateIssueErrorDismissed() {
        _createIssueLiveData.value = null
    }

}