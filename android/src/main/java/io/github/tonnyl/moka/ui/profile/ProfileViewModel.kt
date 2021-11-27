package io.github.tonnyl.moka.ui.profile

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.network.mutations.followUser
import io.github.tonnyl.moka.network.mutations.unfollowUser
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.graphql.OrganizationQuery
import io.tonnyl.moka.graphql.UserQuery
import io.tonnyl.moka.graphql.fragment.Organization
import io.tonnyl.moka.graphql.fragment.User
import io.tonnyl.moka.graphql.fragment.UserStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

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
                ).execute().data?.user?.user

                _userProfile.postValue(Resource.success(user))

                if (user?.viewerCanFollow == true) {
                    _followState.postValue(Resource.success(user.viewerIsFollowing))
                } else {
                    _followState.postValue(Resource.success(null))
                }
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _userProfile.postValue(Resource.error(e, null))
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
                    ).execute().data?.organization?.organization

                _organizationProfile.postValue(Resource.success(org))

                _followState.postValue(Resource.success(null))
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _organizationProfile.postValue(Resource.error(e, null))
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
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _followState.postValue(Resource.error(e, isFollowing))
            }
        }

    }

    @MainThread
    fun updateUserStatusIfNeeded(newStatus: UserStatus?) {
        _userProfile.value?.data?.let { user ->
            if (user.status?.userStatus != newStatus) {
                _userProfile.value =
                    Resource.success(
                        user.copy(
                            status = if (newStatus != null) {
                                user.status?.copy(userStatus = newStatus)
                            } else {
                                null
                            }
                        )
                    )
            }
        }
    }

}