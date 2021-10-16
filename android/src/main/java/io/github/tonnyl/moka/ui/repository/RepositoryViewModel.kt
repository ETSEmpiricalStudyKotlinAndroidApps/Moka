package io.github.tonnyl.moka.ui.repository

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.data.Repository
import io.github.tonnyl.moka.data.toNullableRepository
import io.github.tonnyl.moka.network.mutations.addStar
import io.github.tonnyl.moka.network.mutations.removeStar
import io.github.tonnyl.moka.util.HtmlHandler
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.graphql.RepositoryQuery
import io.tonnyl.moka.graphql.UpdateSubscriptionMutation
import io.tonnyl.moka.graphql.type.SubscriptionState
import io.tonnyl.moka.graphql.type.UpdateSubscriptionInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import java.nio.charset.StandardCharsets

@ExperimentalSerializationApi
class RepositoryViewModel(
    private val accountInstance: AccountInstance,
    private val login: String,
    private val repositoryName: String
) : ViewModel() {

    private val _repository = MutableLiveData<Resource<Repository>>()
    val repository: LiveData<Resource<Repository>>
        get() = _repository

    private val _readmeHtml = MutableLiveData<Resource<String>>()
    val readmeHtml: LiveData<Resource<String>>
        get() = _readmeHtml

    private val _starState = MutableLiveData<Resource<Boolean?>>()
    val starState: LiveData<Resource<Boolean?>>
        get() = _starState

    private val _subscriptionState = MutableLiveData<Resource<SubscriptionState?>>()
    val subscriptionState: LiveData<Resource<SubscriptionState?>>
        get() = _subscriptionState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _repository.postValue(Resource.loading(null))

            try {
                val repo = accountInstance.apolloGraphQLClient
                    .apolloClient
                    .query(
                        query = RepositoryQuery(
                            login = login,
                            repoName = repositoryName
                        )
                    ).data?.repository.toNullableRepository()

                _repository.postValue(Resource.success(repo))
                _starState.postValue(Resource.success(repo?.viewerHasStarred))
                _subscriptionState.postValue(Resource.success(repo?.viewerSubscription))

                repo?.defaultBranchRef?.let { ref ->
                    updateBranchName(ref.name)
                }
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _repository.postValue(Resource.error(e, null))
            }
        }
    }

    private fun updateBranchName(branchName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _readmeHtml.postValue(Resource.loading(null))

            try {
                val response = accountInstance.repositoryContentApi
                    .getReadme(
                        owner = login,
                        repo = repositoryName,
                        ref = branchName
                    )

                _readmeHtml.postValue(
                    Resource.success(
                        HtmlHandler.toHtml(
                            rawText = String(
                                Base64.decode(response.content, Base64.DEFAULT),
                                StandardCharsets.UTF_8
                            ),
                            login = login,
                            repositoryName = repositoryName,
                            branch = branchName
                        )
                    )
                )
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _readmeHtml.postValue(Resource.error(e, null))
            }
        }
    }

    fun toggleStar() {
        if (_starState.value?.status == Status.LOADING) {
            return
        }

        val repositoryId = repository.value?.data?.id ?: return
        val hasStarred = _starState.value?.data ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _starState.postValue(Resource.loading(hasStarred))

            try {
                if (hasStarred) {
                    removeStar(
                        apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                        starrableId = repositoryId
                    )
                } else {
                    addStar(
                        apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                        starrableId = repositoryId
                    )
                }

                _starState.postValue(Resource.success(!hasStarred))
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _starState.postValue(Resource.error(e, hasStarred))
            }
        }
    }

    fun updateSubscription(state: SubscriptionState) {
        val repo = repository.value?.data ?: return
        if (_subscriptionState.value?.status == Status.LOADING
            || !repo.viewerCanSubscribe
            || _subscriptionState.value?.data == state
        ) {
            return
        }

        viewModelScope.launch {
            try {
                _subscriptionState.postValue(Resource.loading(data = _subscriptionState.value?.data))

                accountInstance.apolloGraphQLClient
                    .apolloClient
                    .mutate(
                        mutation = UpdateSubscriptionMutation(
                            input = UpdateSubscriptionInput(
                                state = state,
                                subscribableId = repo.id
                            )
                        )
                    )

                _subscriptionState.postValue(Resource.success(data = state))
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _subscriptionState.postValue(
                    Resource.error(
                        exception = e,
                        data = _subscriptionState.value?.data
                    )
                )
            }
        }
    }

}