package io.github.tonnyl.moka.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.github.tonnyl.moka.UserQuery
import io.github.tonnyl.moka.net.Resource

class UserProfileViewModel(login: String) : ViewModel() {

    val user: LiveData<Resource<UserQuery.Data>> = Transformations.map(UserProfileLiveData(login)) { it }

}