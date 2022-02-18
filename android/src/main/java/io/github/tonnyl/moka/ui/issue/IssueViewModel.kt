package io.github.tonnyl.moka.ui.issue

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.data.IssueTimelineItem
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.graphql.AddCommentMutation
import io.tonnyl.moka.graphql.IssueQuery.Issue
import io.tonnyl.moka.graphql.type.AddCommentInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

@ExperimentalSerializationApi
data class IssueViewModelExtra(
    val accountInstance: AccountInstance,
    val owner: String,
    val name: String,
    val number: Int
)

@ExperimentalSerializationApi
class IssueViewModel(private val extra: IssueViewModelExtra) : ViewModel() {

    private val _issueLiveData = MutableLiveData<Issue>()
    val issueLiveData: LiveData<Issue>
        get() = _issueLiveData

    private val _addedTimelineComments = MutableLiveData(Pair(Long.MIN_VALUE, mutableListOf<IssueTimelineItem>()))

    @Suppress("UNCHECKED_CAST")
    val addedTimelineComments: LiveData<Pair<Long, List<IssueTimelineItem>>>
        get() = _addedTimelineComments as LiveData<Pair<Long, List<IssueTimelineItem>>>

    private val _addCommentResource = MutableLiveData<Resource<Unit>?>()
    val addCommentResource: LiveData<Resource<Unit>?>
        get() = _addCommentResource

    val commentText = mutableStateOf("")

    val issueTimelineFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                IssueTimelineDataSource(
                    apolloClient = extra.accountInstance.apolloGraphQLClient.apolloClient,
                    owner = extra.owner,
                    name = extra.name,
                    number = extra.number,
                    issueData = _issueLiveData
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    fun addComment() {
        val issueId = issueLiveData.value?.id
        if (commentText.value.isEmpty()
            || issueId.isNullOrEmpty()
            || addCommentResource.value?.status == Status.LOADING
        ) {
            return
        }

        viewModelScope.launch {
            try {
                _addCommentResource.value = Resource.loading(data = null)

                val comment = withContext(Dispatchers.IO) {
                    extra.accountInstance.apolloGraphQLClient.apolloClient.mutation(
                        mutation = AddCommentMutation(
                            input = AddCommentInput(
                                body = commentText.value,
                                subjectId = issueId
                            )
                        )
                    ).execute().data?.addComment?.commentEdge?.node?.issueCommentFragment
                }

                val v = _addedTimelineComments.value
                if (comment != null
                    && v != null
                ) {
                    v.second.add(IssueTimelineItem(issueComment = comment))
                    _addedTimelineComments.value = v.copy(v.first + 1, v.second)
                }

                commentText.value = ""

                _addCommentResource.value = Resource.success(data = null)
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) {
                    e.asLog()
                }

                _addCommentResource.value = Resource.error(data = null, exception = e)
            }
        }
    }

    fun onErrorDismissed() {
        _addCommentResource.value = null
    }

    companion object {

        private object IssueViewModelExtraKeyImpl : CreationExtras.Key<IssueViewModelExtra>

        val ISSUE_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<IssueViewModelExtra> =
            IssueViewModelExtraKeyImpl

    }

}