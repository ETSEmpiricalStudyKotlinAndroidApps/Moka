package io.github.tonnyl.moka.ui.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.data.Repository
import io.github.tonnyl.moka.data.toNonNullTreeEntry
import io.github.tonnyl.moka.data.toNullableRepository
import io.github.tonnyl.moka.mutations.AddStarMutation
import io.github.tonnyl.moka.mutations.FollowUserMutation
import io.github.tonnyl.moka.mutations.RemoveStarMutation
import io.github.tonnyl.moka.mutations.UnfollowUserMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.queries.CurrentLevelTreeViewQuery
import io.github.tonnyl.moka.queries.FileContentQuery
import io.github.tonnyl.moka.queries.OrganizationsRepositoryQuery
import io.github.tonnyl.moka.queries.UsersRepositoryQuery
import io.github.tonnyl.moka.type.AddStarInput
import io.github.tonnyl.moka.type.FollowUserInput
import io.github.tonnyl.moka.type.RemoveStarInput
import io.github.tonnyl.moka.type.UnfollowUserInput
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.util.HtmlHandler
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.*

class RepositoryViewModel(
    private val args: RepositoryFragmentArgs
) : ViewModel() {

    private val _usersRepository = MutableLiveData<Resource<Repository>>()
    val userRepository: LiveData<Resource<Repository>>
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

    private val _userEvent = MutableLiveData<Event<RepositoryEvent>>()
    val userEvent: LiveData<Event<RepositoryEvent>>
        get() = _userEvent

    init {
        refresh()
    }

    @MainThread
    private fun refresh() {
        when {
            args.profileType == ProfileType.USER
                    || _usersRepository.value?.data != null -> {
                refreshUsersRepository()
            }
            args.profileType == ProfileType.ORGANIZATION
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
                val response = runBlocking {
                    GraphQLClient.apolloClient
                        .query(CurrentLevelTreeViewQuery(args.login, args.name, "$branchName:"))
                        .execute()
                }

                val readmeFileNames = mapOf(
                    "readme.md" to "md",
                    "readme.html" to "html",
                    "readme.htm" to "html",
                    "readme" to "plain"
                )

                val readmeFile = response.data()
                    ?.repository
                    ?.object_
                    ?.fragments
                    ?.tree
                    ?.entries
                    ?.firstOrNull {
                        readmeFileNames.contains(
                            it.fragments.treeEntry.name.toLowerCase(
                                Locale.US
                            )
                        )
                    }

                val fileEntry = readmeFile?.fragments
                    ?.treeEntry
                    ?.toNonNullTreeEntry()

                if (fileEntry != null) {
                    val expression = "$branchName:${fileEntry.name}"
                    val fileContentResponse = runBlocking {
                        GraphQLClient.apolloClient
                            .query(FileContentQuery(args.login, args.name, expression))
                            .execute()
                    }

                    if (fileContentResponse.hasErrors()) {
                        _readmeHtml.postValue(
                            Resource.error(
                                fileContentResponse.errors().firstOrNull()?.message(),
                                null
                            )
                        )
                    } else {
                        val html =
                            fileContentResponse.data()?.repository?.object_?.fragments?.blob?.text
                        if (html.isNullOrEmpty()) {
                            _readmeHtml.postValue(Resource.success(""))
                        } else {
                            _readmeHtml.postValue(
                                Resource.success(
                                    HtmlHandler.toHtml(html, args.login, args.name, branchName)
                                )
                            )
                        }
                    }
                } else {
                    _readmeHtml.postValue(Resource.success(null))
                }
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
                val mutation = if (hasStarred) {
                    RemoveStarMutation(RemoveStarInput(repositoryId))
                } else {
                    AddStarMutation(AddStarInput(repositoryId))
                }

                val response = runBlocking {
                    GraphQLClient.apolloClient
                        .mutate(mutation)
                        .execute()
                }

                if (response.hasErrors()) {
                    _starState.postValue(
                        Resource.error(response.errors().firstOrNull()?.message(), hasStarred)
                    )
                } else {
                    _starState.postValue(Resource.success(!hasStarred))
                }
            } catch (e: Exception) {
                Timber.e(e, "toggleStar error")

                _starState.postValue(Resource.error(e.message, hasStarred))
            }
        }
    }

    fun toggleFollow() {
        if (args.profileType == ProfileType.ORGANIZATION
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
                val mutation = if (isFollowing) {
                    UnfollowUserMutation(UnfollowUserInput(userId))
                } else {
                    FollowUserMutation(FollowUserInput(userId))
                }
                val response = runBlocking {
                    GraphQLClient.apolloClient
                        .mutate(mutation)
                        .execute()
                }

                if (response.hasErrors()) {
                    _followState.postValue(
                        Resource.error(response.errors().firstOrNull()?.message(), isFollowing)
                    )
                } else {
                    _followState.postValue(Resource.success(!isFollowing))
                }
            } catch (e: Exception) {
                Timber.e(e, "toggleFollow error")

                _followState.postValue(Resource.error(e.message, isFollowing))
            }
        }
    }

    @MainThread
    fun userEvent(event: RepositoryEvent) {
        _userEvent.value = Event(event)
    }

    private fun refreshUsersRepository() {
        viewModelScope.launch(Dispatchers.IO) {
            _usersRepository.postValue(Resource.loading(null))

            try {
                val response = runBlocking {
                    GraphQLClient.apolloClient
                        .query(UsersRepositoryQuery(args.login, args.name))
                        .execute()
                }

                val repo = response.data().toNullableRepository()

                if (repo == null) {
                    _usersRepository.postValue(
                        Resource.error(response.errors().firstOrNull()?.message(), null)
                    )
                } else {
                    _usersRepository.postValue(Resource.success(repo))

                    repo.defaultBranchRef?.let { ref ->
                        updateBranchName(ref.name)
                    }

                    _followState.postValue(
                        Resource.success(
                            if (repo.viewerCanFollow) {
                                repo.viewerIsFollowing
                            } else {
                                null
                            }
                        )
                    )
                }
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
                val response = runBlocking {
                    GraphQLClient.apolloClient
                        .query(OrganizationsRepositoryQuery(args.login, args.name))
                        .execute()
                }

                val repo = response.data().toNullableRepository()

                if (repo == null) {
                    _organizationsRepository.postValue(
                        Resource.error(response.errors().firstOrNull()?.message(), null)
                    )
                } else {
                    _organizationsRepository.postValue(Resource.success(repo))

                    repo.defaultBranchRef?.let {
                        updateBranchName(it.name)
                    }

                    _followState.postValue(Resource.success(null))
                }
            } catch (e: Exception) {
                Timber.e(e, "refreshOrganizationsRepository error")

                _organizationsRepository.postValue(Resource.error(e.message, null))
            }
        }
    }

}