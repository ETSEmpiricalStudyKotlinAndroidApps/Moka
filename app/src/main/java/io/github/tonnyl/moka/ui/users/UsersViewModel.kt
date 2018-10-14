package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.api.Response
import io.github.tonnyl.moka.FollowersQuery
import io.github.tonnyl.moka.FollowingQuery

class UsersViewModel(private val login: String) : ViewModel() {

    val followingResults: LiveData<Response<FollowingQuery.Data>> = Transformations.map(FollowingLiveData(login)) { it }

    val followersResults: LiveData<Response<FollowersQuery.Data>> = Transformations.map(FollowersLiveData(login)) { it }

}