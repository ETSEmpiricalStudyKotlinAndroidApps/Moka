package io.github.tonnyl.moka.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.net.Resource

class AuthViewModel : ViewModel() {

    private val authLiveData = AuthLiveData()

    val accessTokenResult: LiveData<Resource<Pair<String, AuthenticatedUser>>> = Transformations.map(authLiveData) { it }

    fun getAccessToken(code: String, state: String) = authLiveData.getAccessToken(code, state)

}