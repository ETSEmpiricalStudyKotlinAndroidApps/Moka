package io.github.tonnyl.moka.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.github.tonnyl.moka.data.AccessToken
import io.github.tonnyl.moka.net.Resource

class AuthViewModel : ViewModel() {

    private val authLiveData = AuthLiveData()

    val accessTokenResult: LiveData<Resource<AccessToken>> = Transformations.map(authLiveData) { it }

    fun getAccessToken(code: String) = authLiveData.getAccessToken(code)

}