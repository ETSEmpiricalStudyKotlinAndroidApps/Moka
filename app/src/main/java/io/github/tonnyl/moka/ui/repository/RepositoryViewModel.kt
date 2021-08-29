package io.github.tonnyl.moka.ui.repository

import android.util.Base64
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.data.Repository
import io.github.tonnyl.moka.data.toNullableRepository
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.network.mutations.addStar
import io.github.tonnyl.moka.network.mutations.followUser
import io.github.tonnyl.moka.network.mutations.removeStar
import io.github.tonnyl.moka.network.mutations.unfollowUser
import io.github.tonnyl.moka.queries.OrganizationsRepositoryQuery
import io.github.tonnyl.moka.queries.UsersRepositoryQuery
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.util.HtmlHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber
import java.nio.charset.StandardCharsets

@ExperimentalSerializationApi
class RepositoryViewModel(
    private val accountInstance: AccountInstance,
    private val login: String,
    private val repositoryName: String,
    private val profileType: ProfileType
) : ViewModel() {

    private val _usersRepository = MutableLiveData<Resource<Repository>>()
    val usersRepository: LiveData<Resource<Repository>>
        get() = _usersRepository

    private val _organizationsRepository = MutableLiveData<Resource<Repository>>()
    val organizationsRepository: LiveData<Resource<Repository>>
        get() = _organizationsRepository

    private val _readmeHtml = MutableLiveData<Resource<String>>()
    val readmeHtml: LiveData<Resource<String>>
        get() = _readmeHtml

    private val _starState = MutableLiveData<Resource<Boolean?>>()
    val starState: LiveData<Resource<Boolean?>>
        get() = _starState

    private val _followState = MutableLiveData<Resource<Boolean?>>()
    val followState: LiveData<Resource<Boolean?>>
        get() = _followState

    init {
        refresh()
    }

    @MainThread
    private fun refresh() {
        when {
            profileType == ProfileType.USER
                    || _usersRepository.value?.data != null -> {
                refreshUsersRepository()
            }
            profileType == ProfileType.ORGANIZATION
                    || _organizationsRepository.value?.data != null -> {
                refreshOrganizationsRepository()
            }
            // including ProfileType.NOT_SPECIFIED
            else -> {
                refreshUsersRepository()
                refreshOrganizationsRepository()
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
                Timber.e(e)

                _readmeHtml.postValue(Resource.error(e.message, null))
            }
        }
    }

    fun toggleStar() {
        if (_starState.value?.status == Status.LOADING) {
            return
        }

        val repositoryId = _usersRepository.value?.data?.id ?: return
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
                Timber.e(e, "toggleStar error")

                _starState.postValue(Resource.error(e.message, hasStarred))
            }
        }
    }

    fun toggleFollow() {
        if (profileType == ProfileType.ORGANIZATION
            || _usersRepository.value?.data?.viewerCanFollow != true
            || _followState.value?.status == Status.LOADING
        ) {
            return
        }

        val userId = _usersRepository.value?.data?.id ?: return
        val isFollowing = _followState.value?.data ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _followState.postValue(Resource.loading(isFollowing))

            try {
                if (isFollowing) {
                    unfollowUser(
                        apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                        userId = userId
                    )
                } else {
                    followUser(
                        apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                        userId = userId
                    )
                }

                _followState.postValue(Resource.success(!isFollowing))
            } catch (e: Exception) {
                Timber.e(e, "toggleFollow error")

                _followState.postValue(Resource.error(e.message, isFollowing))
            }
        }
    }

    private fun refreshUsersRepository() {
        viewModelScope.launch(Dispatchers.IO) {
            _usersRepository.postValue(Resource.loading(null))

            try {
                val repo = accountInstance.apolloGraphQLClient
                    .apolloClient
                    .query(
                        query = UsersRepositoryQuery(
                            login = login,
                            repoName = repositoryName
                        )
                    ).data.toNullableRepository()

                _usersRepository.postValue(Resource.success(repo))

                repo?.defaultBranchRef?.let { ref ->
                    updateBranchName(ref.name)
                }

                _followState.postValue(
                    Resource.success(
                        if (repo?.viewerCanFollow == true) {
                            repo.viewerIsFollowing
                        } else {
                            null
                        }
                    )
                )
            } catch (e: Exception) {
                Timber.e(e, "refreshUsersRepositoryData error")

                _usersRepository.postValue(Resource.error(e.message, null))
            }
        }
    }

    private fun refreshOrganizationsRepository() {
        viewModelScope.launch(Dispatchers.IO) {
            _organizationsRepository.postValue(Resource.loading(null))

            try {
                val repo = accountInstance.apolloGraphQLClient
                    .apolloClient
                    .query(
                        query = OrganizationsRepositoryQuery(
                            login = login,
                            repoName = repositoryName
                        )
                    ).data.toNullableRepository()

                _organizationsRepository.postValue(Resource.success(repo))

                repo?.defaultBranchRef?.let {
                    updateBranchName(it.name)
                }

                _followState.postValue(Resource.success(null))
            } catch (e: Exception) {
                Timber.e(e, "refreshOrganizationsRepository error")

                _organizationsRepository.postValue(Resource.error(e.message, null))
            }
        }
    }

}