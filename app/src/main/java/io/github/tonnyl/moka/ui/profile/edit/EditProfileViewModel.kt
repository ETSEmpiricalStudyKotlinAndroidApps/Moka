package io.github.tonnyl.moka.ui.profile.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

@ExperimentalSerializationApi
class EditProfileViewModel(
    private val accountInstance: AccountInstance,
    initialName: String?,
    initialBio: String?,
    initialUrl: String?,
    initialCompany: String?,
    initialLocation: String?,
    initialTwitter: String?
) : ViewModel() {

    private val _loadingStatus = MutableLiveData<Resource<Unit>>()
    val loadingStatus: LiveData<Resource<Unit>>
        get() = _loadingStatus

    private val _name = MutableLiveData(initialName)
    val name: LiveData<String?>
        get() = _name

    private val _bio = MutableLiveData(initialBio)
    val bio: LiveData<String?>
        get() = _bio

    private val _url = MutableLiveData(initialUrl)
    val url: LiveData<String?>
        get() = _url

    private val _company = MutableLiveData(initialCompany)
    val company: LiveData<String?>
        get() = _company

    private val _location = MutableLiveData(initialLocation)
    val location: LiveData<String?>
        get() = _location

    private val _twitterUsername = MutableLiveData(initialTwitter)
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
                    accountInstance.userApi.updateUseInformation(body)
                }

                _loadingStatus.value = Resource.success(null)
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _loadingStatus.value = Resource.error(null, null)
            }
        }
    }

}