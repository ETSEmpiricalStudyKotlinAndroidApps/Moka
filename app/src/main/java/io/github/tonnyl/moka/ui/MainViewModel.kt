package io.github.tonnyl.moka.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.coroutines.toDeferred
import io.github.tonnyl.moka.ViewerQuery
import io.github.tonnyl.moka.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainViewModel : ViewModel() {

    val login = MutableLiveData<String?>()
    val userId = MutableLiveData<Long?>()

    val loginUserProfile = MutableLiveData<ViewerQuery.Data?>()

    fun getUserProfile() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apolloClient
                        .query(
                            ViewerQuery.builder()
                                .build()
                        ).toDeferred()
                }.await()

                Timber.d("get viewer info call success, resp = $response")

                loginUserProfile.value = response.data()
            } catch (e: Exception) {
                Timber.e(e, "get viewer info call error: ${e.message}")

            }
        }
    }

}