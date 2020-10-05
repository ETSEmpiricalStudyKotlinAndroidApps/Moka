package io.github.tonnyl.moka.ui.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.data.Repository
import io.github.tonnyl.moka.data.toNonNullTreeEntry
import io.github.tonnyl.moka.data.toNullableRepository
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.network.mutations.addStar
import io.github.tonnyl.moka.network.mutations.followUser
import io.github.tonnyl.moka.network.mutations.removeStar
import io.github.tonnyl.moka.network.mutations.unfollowUser
import io.github.tonnyl.moka.network.queries.queryCurrentLevelTreeView
import io.github.tonnyl.moka.network.queries.queryFileContent
import io.github.tonnyl.moka.network.queries.queryOrganizationsRepository
import io.github.tonnyl.moka.network.queries.queryUsersRepository
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.repository.RepositoryEvent.*
import io.github.tonnyl.moka.util.HtmlHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class RepositoryViewModel(
    private val args: RepositoryFragmentArgs
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
                val response = queryCurrentLevelTreeView(args.login, args.name, "$branchName:")

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
                    val fileContentResponse = queryFileContent(args.login, args.name, expression)

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
                if (hasStarred) {
                    removeStar(repositoryId)
                } else {
                    addStar(repositoryId)
                }

                _starState.postValue(Resource.success(!hasStarred))
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
                if (isFollowing) {
                    unfollowUser(userId)
                } else {
                    followUser(userId)
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
                val repo = queryUsersRepository(args.login, args.name)
                    .data()
                    .toNullableRepository()

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
                val repo = queryOrganizationsRepository(args.login, args.name)
                    .data()
                    .toNullableRepository()

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

    @MainThread
    fun viewOwnersProfile() {
        val profileType = if (args.profileType == ProfileType.NOT_SPECIFIED) {
            if (usersRepository.value != null) {
                ProfileType.USER
            } else {
                ProfileType.ORGANIZATION
            }
        } else {
            args.profileType
        }
        _userEvent.value = Event(ViewOwnersProfile(profileType))
    }

    @MainThread
    fun viewWatchers() {
        _userEvent.value = Event(ViewWatchers)
    }

    @MainThread
    fun viewStargazers() {
        _userEvent.value = Event(ViewStargazers)
    }

    @MainThread
    fun viewForks() {
        _userEvent.value = Event(ViewForks)
    }

    @MainThread
    fun viewIssues() {
        _userEvent.value = Event(ViewIssues)
    }

    @MainThread
    fun viewPullRequests() {
        _userEvent.value = Event(ViewPullRequests)
    }

    @MainThread
    fun viewProjects() {
        _userEvent.value = Event(ViewProjects)
    }

    @MainThread
    fun viewLicense() {
        _userEvent.value = Event(ViewLicense)
    }

    @MainThread
    fun viewBranches() {
        _userEvent.value = Event(ViewBranches)
    }

    @MainThread
    fun viewAllTopics() {
        _userEvent.value = Event(ViewAllTopics)
    }

    @MainThread
    fun viewReleases() {
        _userEvent.value = Event(ViewReleases)
    }

    @MainThread
    fun viewLanguages() {
        _userEvent.value = Event(ViewLanguages)
    }

    @MainThread
    fun viewFiles() {
        _userEvent.value = Event(ViewFiles)
    }

}