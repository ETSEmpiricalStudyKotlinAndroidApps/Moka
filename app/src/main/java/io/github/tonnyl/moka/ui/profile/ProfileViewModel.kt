package io.github.tonnyl.moka.ui.profile

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.data.*
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.network.mutations.addStar
import io.github.tonnyl.moka.network.mutations.followUser
import io.github.tonnyl.moka.network.mutations.removeStar
import io.github.tonnyl.moka.network.mutations.unfollowUser
import io.github.tonnyl.moka.network.queries.queryOrganization
import io.github.tonnyl.moka.network.queries.queryUser
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.profile.ProfileEvent.*
import io.github.tonnyl.moka.util.updateOnAnyThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileViewModel(
    private val args: ProfileFragmentArgs
) : ViewModel() {

    private val _userProfile = MutableLiveData<Resource<User>>()
    val userProfile: LiveData<Resource<User>>
        get() = _userProfile

    private val _organizationProfile = MutableLiveData<Resource<Organization>>()
    val organizationProfile: LiveData<Resource<Organization>>
        get() = _organizationProfile

    private val _followState = MutableLiveData<Resource<Boolean?>>(null)
    val followState: LiveData<Resource<Boolean?>>
        get() = _followState

    private val _userEvent = MutableLiveData<Event<ProfileEvent>>()
    val userEvent: LiveData<Event<ProfileEvent>>
        get() = _userEvent

    init {
        refreshData()
    }

    fun refreshData() {
        when {
            args.profileType == ProfileType.USER
                    || _userProfile.value?.data != null -> {
                refreshUserProfile()
            }
            args.profileType == ProfileType.ORGANIZATION
                    || _organizationProfile.value?.data != null -> {
                refreshOrganization()
            }
            else -> {
                refreshUserProfile()
                refreshOrganization()
            }
        }
    }

    private fun refreshUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            _userProfile.postValue(Resource.loading(null))

            try {
                val user = queryUser(args.login)
                    .data()
                    ?.user
                    ?.fragments
                    ?.user
                    ?.toNonNullUser()

                _userProfile.postValue(Resource.success(user))

                if (user?.viewerCanFollow == true) {
                    _followState.postValue(Resource.success(user.viewerIsFollowing))
                } else {
                    _followState.postValue(Resource.success(null))
                }
            } catch (e: Exception) {
                Timber.e(e)

                _userProfile.postValue(Resource.error(e.message, null))
            }
        }
    }

    private fun refreshOrganization() {
        viewModelScope.launch(Dispatchers.IO) {
            _organizationProfile.postValue(Resource.loading(null))

            try {
                val response = queryOrganization(args.login)

                val org = response.data()?.organization.toNullableOrganization()

                _organizationProfile.postValue(Resource.success(org))

                _followState.postValue(Resource.success(null))
            } catch (e: Exception) {
                Timber.e(e)

                _organizationProfile.postValue(Resource.error(e.message, null))
            }
        }
    }

    fun toggleFollow() {
        if (_followState.value?.status == Status.LOADING) {
            return
        }

        val user = _userProfile.value?.data ?: return
        val isFollowing = _followState.value?.data ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _followState.postValue(Resource.loading(isFollowing))

            try {
                if (isFollowing) {
                    unfollowUser(user.id)
                } else {
                    followUser(user.id)
                }

                _followState.postValue(Resource.success(!isFollowing))
            } catch (e: Exception) {
                Timber.e(e, "toggleFollow failed")

                _followState.postValue(Resource.error(e.message, isFollowing))
            }
        }

    }

    @MainThread
    fun updateUserStatusIfNeeded(status: UserStatus?) {
        _userProfile.value?.data?.let { user ->
            if (user.status != status) {
                _userProfile.value = Resource.success(user.copy(status = status))
            }
        }
    }

    fun viewRepositories() {
        _userEvent.updateOnAnyThread(Event(ViewRepositories))
    }

    fun viewStars() {
        _userEvent.updateOnAnyThread(Event(ViewStars))
    }

    fun viewFollowers() {
        _userEvent.updateOnAnyThread(Event(ViewFollowers))
    }

    fun viewFollowings() {
        _userEvent.updateOnAnyThread(Event(ViewFollowings))
    }

    fun viewProjects() {
        _userEvent.updateOnAnyThread(Event(ViewProjects))
    }

    fun editStatus() {
        _userEvent.updateOnAnyThread(Event(EditStatus))
    }

    fun viewRepository(repository: RepositoryItem) {
        _userEvent.updateOnAnyThread(Event(ViewRepository(repository)))
    }

    fun viewGist(gist: Gist2) {
        _userEvent.updateOnAnyThread(Event(ViewGist(gist)))
    }

    fun starRepository(repository: RepositoryItem) {
        starRepositoryOrGist(repository)
    }

    fun starGist(gist2: Gist2) {
        starRepositoryOrGist(gist2)
    }

    private fun starRepositoryOrGist(pinnableItem: PinnableItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val (viewerHasStarred, pinnableItemsId) = when (pinnableItem) {
                    is RepositoryItem -> {
                        Pair(pinnableItem.viewerHasStarred, pinnableItem.id)
                    }
                    is Gist2 -> {
                        Pair(pinnableItem.viewerHasStarred, pinnableItem.id)
                    }
                    else -> {
                        throw IllegalStateException("unknown PinnableItem type")
                    }
                }
                if (viewerHasStarred) {
                    removeStar(pinnableItemsId)
                } else {
                    addStar(pinnableItemsId)
                }

                val pinnedItems = userProfile.value?.data?.pinnedItems
                    ?: organizationProfile.value?.data?.pinnedItems
                if (!pinnedItems.isNullOrEmpty()) {
                    pinnedItems.indexOfFirst {
                        if (pinnableItem is RepositoryItem) {
                            it is RepositoryItem && it.id == pinnableItemsId
                        } else {
                            it is Gist2 && it.id == pinnableItemsId
                        }
                    }.let {
                        if (it >= 0) {
                            if (pinnableItem is RepositoryItem) {
                                pinnedItems[it] = pinnableItem.copy(
                                    viewerHasStarred = !pinnableItem.viewerHasStarred
                                )
                            } else if (pinnableItem is Gist2) {
                                pinnedItems[it] = pinnableItem.copy(
                                    viewerHasStarred = !pinnableItem.viewerHasStarred
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

}