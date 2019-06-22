package io.github.tonnyl.moka.ui.profile

import androidx.lifecycle.*
import com.apollographql.apollo.coroutines.toDeferred
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.UserQuery
import io.github.tonnyl.moka.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class UserProfileViewModel(
    private val login: String
) : ViewModel() {

    private val _userProfile = MutableLiveData<Resource<UserQuery.Data>>()
    val userProfile: LiveData<Resource<UserQuery.Data>>
        get() = _userProfile

    init {
        refreshUserProfile()
    }

    fun refreshUserProfile() {
        viewModelScope.launch(Dispatchers.Main) {
            _userProfile.value = Resource.loading(null)

            try {
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apolloClient
                        .query(
                            UserQuery.builder()
                                .login(login)
                                .build()
                        ).toDeferred()
                }.await()

                _userProfile.value = Resource.success(response.data())
            } catch (e: Exception) {
                Timber.e(e)

                _userProfile.value = Resource.error(e.message, null)
            }
        }
    }

}