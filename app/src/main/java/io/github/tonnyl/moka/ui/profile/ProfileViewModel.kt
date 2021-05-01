package io.github.tonnyl.moka.ui.profile

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.data.*
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.network.mutations.addStar
import io.github.tonnyl.moka.network.mutations.followUser
import io.github.tonnyl.moka.network.mutations.removeStar
import io.github.tonnyl.moka.network.mutations.unfollowUser
import io.github.tonnyl.moka.queries.OrganizationQuery
import io.github.tonnyl.moka.queries.UserQuery
import io.github.tonnyl.moka.queries.UserQuery.Data.User.Companion.user
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber

@ExperimentalSerializationApi
class ProfileViewModel(
    private val accountInstance: AccountInstance,
    private val login: String,
    private val profileType: ProfileType
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

    init {
        refreshData()
    }

    fun refreshData() {
        when {
            profileType == ProfileType.USER
                    || _userProfile.value?.data != null -> {
                refreshUserProfile()
            }
            profileType == ProfileType.ORGANIZATION
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
                val user = accountInstance.apolloGraphQLClient.apolloClient.query(
                    UserQuery(login)
                ).data?.user?.user()?.toNonNullUser()

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
                val org = accountInstance.apolloGraphQLClient
                    .apolloClient.query(
                        query = OrganizationQuery(login = login)
                    ).data?.organization.toNullableOrganization()

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
                    unfollowUser(
                        apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                        userId = user.id
                    )
                } else {
                    followUser(
                        apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                        userId = user.id
                    )
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
                    removeStar(
                        apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                        starrableId = pinnableItemsId
                    )
                } else {
                    addStar(
                        apolloClient = accountInstance.apolloGraphQLClient.apolloClient,
                        starrableId = pinnableItemsId
                    )
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