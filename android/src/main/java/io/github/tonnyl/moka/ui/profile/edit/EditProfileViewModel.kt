package io.github.tonnyl.moka.ui.profile.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

data class EditProfileViewModelExtra(
    val accountInstance: AccountInstance,
    val initialName: String?,
    val initialBio: String?,
    val initialUrl: String?,
    val initialCompany: String?,
    val initialLocation: String?,
    val initialTwitter: String?
)

class EditProfileViewModel(private val extra: EditProfileViewModelExtra) : ViewModel() {

    private val _loadingStatus = MutableLiveData<Resource<Unit>?>()
    val loadingStatus: LiveData<Resource<Unit>?>
        get() = _loadingStatus

    private val _name = MutableLiveData(extra.initialName)
    val name: LiveData<String?>
        get() = _name

    private val _bio = MutableLiveData(extra.initialBio)
    val bio: LiveData<String?>
        get() = _bio

    private val _url = MutableLiveData(extra.initialUrl)
    val url: LiveData<String?>
        get() = _url

    private val _company = MutableLiveData(extra.initialCompany)
    val company: LiveData<String?>
        get() = _company

    private val _location = MutableLiveData(extra.initialLocation)
    val location: LiveData<String?>
        get() = _location

    private val _twitterUsername = MutableLiveData(extra.initialTwitter)
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

                withContext(Dispatchers.IO) {
                    extra.accountInstance.userApi.updateUseInformation(body)
                }

                _loadingStatus.value = Resource.success(null)
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _loadingStatus.value = Resource.error(null, null)
            }
        }
    }

    fun onErrorDismissed() {
        _loadingStatus.value = null
    }

}