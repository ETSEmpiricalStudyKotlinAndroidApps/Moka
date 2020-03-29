package io.github.tonnyl.moka.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.tonnyl.moka.data.AuthenticatedUser

class MainViewModel : ViewModel() {

    val currentUser = MutableLiveData<AuthenticatedUser>()

    private val _event = MutableLiveData<Event<SearchBarEvent>>()
    val event: LiveData<Event<SearchBarEvent>>
        get() = _event

    fun getUserProfile() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val response = runBlocking {
//                    GraphQLClient.apolloClient
//                        .query(
//                            ViewerQuery.builder()
//                                .build()
//                        )
//                        .execute()
//                }
//
//                Timber.d("get viewer info call success, resp = $response")
//
//                loginUserProfile.postValue(response.data())
//            } catch (e: Exception) {
//                Timber.e(e, "get viewer info call error: ${e.message}")
//
//            }
//        }
    }

    fun showUI(type: SearchBarEvent) {
        _event.value = Event(type)
    }

}