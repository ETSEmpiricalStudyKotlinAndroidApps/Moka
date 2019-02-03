package io.github.tonnyl.moka.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.api.Response
import io.github.tonnyl.moka.UserQuery

class UserProfileViewModel(login: String) : ViewModel() {

    val user: LiveData<Response<UserQuery.Data>> = Transformations.map(UserProfileLiveData(login)) { it }

}