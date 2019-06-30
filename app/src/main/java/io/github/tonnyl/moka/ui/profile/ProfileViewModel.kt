package io.github.tonnyl.moka.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.toDeferred
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.OrganizationQuery
import io.github.tonnyl.moka.UserQuery
import io.github.tonnyl.moka.data.Organization
import io.github.tonnyl.moka.data.UserGraphQL
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ProfileViewModel(
    private val login: String,
    private val profileType: ProfileType
) : ViewModel() {

    private val _loadStatusLiveData = MutableLiveData<Resource<Unit>>()
    val loadStatusLiveData: LiveData<Resource<Unit>>
        get() = _loadStatusLiveData

    private val _userProfile = MutableLiveData<UserGraphQL?>()
    val userProfile: LiveData<UserGraphQL?>
        get() = _userProfile

    private val _organizationProfile = MutableLiveData<Organization?>()
    val organizationProfile: LiveData<Organization?>
        get() = _organizationProfile

    private val _initLoadStatus = MutableLiveData<Resource<Unit>>()
    val initLoadStatus: LiveData<Resource<Unit>>
        get() = _initLoadStatus

    init {
        _initLoadStatus.value = Resource.loading(Unit)

        refreshData()
    }

    fun refreshData() {
        when (profileType) {
            ProfileType.USER -> {
                refreshUserProfile()
            }
            ProfileType.ORGANIZATION -> {
                refreshOrganization()
            }
            ProfileType.NOT_SPECIFIED -> {
                refreshUserProfile()
                refreshOrganization()
            }
        }
    }

    private fun refreshUserProfile() {
        viewModelScope.launch(Dispatchers.Main) {
            _loadStatusLiveData.value = Resource.loading(null)

            try {
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apolloClient
                        .query(
                            UserQuery.builder()
                                .login(login)
                                .build()
                        ).toDeferred()
                }.await()

                if (_initLoadStatus.value?.status == Status.LOADING) {
                    _initLoadStatus.value = Resource.success(Unit)
                }

                _loadStatusLiveData.value = Resource.success(Unit)
                _userProfile.value = UserGraphQL.createFromRaw(response.data()?.user())
            } catch (e: Exception) {
                Timber.e(e)

                if (_initLoadStatus.value?.status == Status.LOADING) {
                    _initLoadStatus.value = Resource.error(e.message, Unit)
                }

                _loadStatusLiveData.value = Resource.error(e.message, null)
                _userProfile.value = null
            }
        }
    }

    private fun refreshOrganization() {
        viewModelScope.launch(Dispatchers.Main) {
            _loadStatusLiveData.value = Resource.loading(null)

            try {
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apolloClient
                        .query(
                            OrganizationQuery.builder()
                                .login(login)
                                .build()
                        ).toDeferred()
                }.await()

                if (_initLoadStatus.value?.status == Status.LOADING) {
                    _initLoadStatus.value = Resource.success(Unit)
                }

                _loadStatusLiveData.value = Resource.success(Unit)
                _organizationProfile.value =
                    Organization.createFromRaw(response.data()?.organization())
            } catch (e: Exception) {
                Timber.e(e)

                if (_initLoadStatus.value?.status == Status.LOADING) {
                    _initLoadStatus.value = Resource.error(e.message, null)
                }

                _loadStatusLiveData.value = Resource.error(e.message, null)
                _organizationProfile.value = null
            }
        }
    }

}