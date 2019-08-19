package io.github.tonnyl.moka.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.ViewerQuery
import io.github.tonnyl.moka.network.NetworkClient
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class MainViewModel : ViewModel() {

    val login = MutableLiveData<String?>()
    val userId = MutableLiveData<Long?>()

    val loginUserProfile = MutableLiveData<ViewerQuery.Data?>()

    fun getUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = runBlocking {
                    NetworkClient.apolloClient
                        .query(
                            ViewerQuery.builder()
                                .build()
                        )
                        .execute()
                }

                Timber.d("get viewer info call success, resp = $response")

                loginUserProfile.postValue(response.data())
            } catch (e: Exception) {
                Timber.e(e, "get viewer info call error: ${e.message}")

            }
        }
    }

}