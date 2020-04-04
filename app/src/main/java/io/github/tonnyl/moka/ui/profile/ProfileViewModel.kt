package io.github.tonnyl.moka.ui.profile

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.data.Organization
import io.github.tonnyl.moka.data.User
import io.github.tonnyl.moka.data.toNonNullUser
import io.github.tonnyl.moka.data.toNullableOrganization
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.network.mutations.followUser
import io.github.tonnyl.moka.network.mutations.unfollowUser
import io.github.tonnyl.moka.queries.OrganizationQuery
import io.github.tonnyl.moka.queries.UserQuery
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
                val response = runBlocking {
                    GraphQLClient.apolloClient
                        .query(UserQuery(args.login))
                        .execute()
                }

                val user = response.data()?.user?.fragments?.user?.toNonNullUser()

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
                val response = runBlocking {
                    GraphQLClient.apolloClient
                        .query(OrganizationQuery(args.login))
                        .execute()
                }

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
    fun userEvent(event: ProfileEvent) {
        _userEvent.value = Event(event)
    }

}