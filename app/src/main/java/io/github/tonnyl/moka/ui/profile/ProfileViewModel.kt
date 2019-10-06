package io.github.tonnyl.moka.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.OrganizationQuery
import io.github.tonnyl.moka.UserQuery
import io.github.tonnyl.moka.data.Organization
import io.github.tonnyl.moka.data.User
import io.github.tonnyl.moka.data.toNonNullUser
import io.github.tonnyl.moka.data.toNullableOrganization
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class ProfileViewModel(
    private val login: String,
    private val profileType: ProfileType
) : ViewModel() {

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?>
        get() = _userProfile

    private val _organizationProfile = MutableLiveData<Organization?>()
    val organizationProfile: LiveData<Organization?>
        get() = _organizationProfile

    private val _loadStatus = MutableLiveData<Resource<Unit>>()
    val loadStatus: LiveData<Resource<Unit>>
        get() = _loadStatus

    init {
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
        viewModelScope.launch(Dispatchers.IO) {
            _loadStatus.postValue(Resource.loading(null))

            try {
                val response = GraphQLClient.apolloClient
                    .query(
                        UserQuery.builder()
                            .login(login)
                            .build()
                    )
                    .execute()

                _loadStatus.postValue(Resource.success(Unit))
                _userProfile.postValue(response.data()?.user()?.fragments()?.user()?.toNonNullUser())
            } catch (e: Exception) {
                Timber.e(e)

                _loadStatus.postValue(Resource.error(e.message, null))
                // don't send null value to keep data shown in the screen.
            }
        }
    }

    private fun refreshOrganization() {
        viewModelScope.launch(Dispatchers.IO) {
            _loadStatus.postValue(Resource.loading(null))

            try {
                val response = runBlocking {
                    GraphQLClient.apolloClient
                        .query(
                            OrganizationQuery.builder()
                                .login(login)
                                .build()
                        )
                        .execute()
                }

                _loadStatus.postValue(Resource.success(Unit))
                _organizationProfile.postValue(
                    response.data()?.organization().toNullableOrganization()
                )
            } catch (e: Exception) {
                Timber.e(e)

                _loadStatus.postValue(Resource.error(e.message, null))
                // don't send null value to keep data shown in the screen.
            }
        }
    }

}