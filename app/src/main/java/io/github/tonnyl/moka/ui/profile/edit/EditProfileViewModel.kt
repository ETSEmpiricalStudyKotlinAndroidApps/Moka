package io.github.tonnyl.moka.ui.profile.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.network.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Resource<Unit>>()
    val loadingStatus: LiveData<Resource<Unit>>
        get() = _loadingStatus

    private val service: UserService by lazy(LazyThreadSafetyMode.NONE) {
        RetrofitClient.createService(UserService::class.java)
    }

    fun updateUserInformation(
            name: String?,
            email: String,
            url: String?,
            company: String?,
            location: String?,
            bio: String?
    ) {
        _loadingStatus.value = Resource.loading(null)

        viewModelScope.launch {
            val body = mapOf(Pair("name", name), Pair("email", email), Pair("url", url), Pair("company", company), Pair("location", location), Pair("bio", bio))

            val updateResponse = withContext(Dispatchers.IO) {
                service.updateUseInformationAsync(body)
            }.await()

            _loadingStatus.value = if (updateResponse.isSuccessful) {
                Resource(Status.SUCCESS, Unit, null)
            } else {
                Resource(Status.ERROR, Unit, updateResponse.errorBody()?.string())
            }
        }
    }

}