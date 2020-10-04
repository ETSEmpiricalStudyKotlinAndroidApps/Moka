package io.github.tonnyl.moka.ui.profile.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class EditProfileViewModel(
    val args: EditProfileFragmentArgs
) : ViewModel() {

    private val _loadingStatus = MutableLiveData<Resource<Unit>>()
    val loadingStatus: LiveData<Resource<Unit>>
        get() = _loadingStatus

    private val service: UserService by lazy(LazyThreadSafetyMode.NONE) {
        RetrofitClient.createService(UserService::class.java)
    }

    private val _name = MutableLiveData(args.name)
    val name: LiveData<String?>
        get() = _name

    private val _bio = MutableLiveData(args.bio)
    val bio: LiveData<String?>
        get() = _bio

    private val _url = MutableLiveData(args.url)
    val url: LiveData<String?>
        get() = _url

    private val _company = MutableLiveData(args.company)
    val company: LiveData<String?>
        get() = _company

    private val _location = MutableLiveData(args.location)
    val location: LiveData<String?>
        get() = _location

    private val _twitterUsername = MutableLiveData(args.twitter)
    val twitterUsername: LiveData<String?>
        get() = _twitterUsername

    fun updateLocal(
        name: String? = null,
        url: String? = null,
        company: String? = null,
        location: String? = null,
        bio: String? = null,
        twitterUsername: String? = null
    ) {
        name?.let {
            _name.value = it
        }
        url?.let {
            _url.value = it
        }
        company?.let {
            _company.value = it
        }
        location?.let {
            _location.value = it
        }
        bio?.let {
            _bio.value = it
        }
        twitterUsername?.let {
            _twitterUsername.value = it
        }
    }

    fun updateUserInformation() {
        _loadingStatus.value = Resource.loading(null)

        viewModelScope.launch {
            try {
                val body = mapOf(
                    "name" to name.value,
                    "url" to url.value,
                    "company" to company.value,
                    "location" to location.value,
                    "bio" to bio.value,
                    "twitter_username" to twitterUsername.value
                )

                val updateResponse = withContext(Dispatchers.IO) {
                    service.updateUseInformation(body)
                }

                _loadingStatus.value = if (updateResponse.isSuccessful) {
                    Resource.success(null)
                } else {
                    Resource.error(null, null)
                }
            } catch (e: Exception) {
                Timber.e(e)
                _loadingStatus.value = Resource.error(null, null)
            }
        }
    }

}