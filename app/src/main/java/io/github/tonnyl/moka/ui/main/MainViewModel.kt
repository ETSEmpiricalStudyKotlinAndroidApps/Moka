package io.github.tonnyl.moka.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.ViewerQuery
import timber.log.Timber

class MainViewModel : ViewModel() {

    val login = MutableLiveData<String?>()
    val loginUserProfile = MutableLiveData<ViewerQuery.Data?>()

    private var getViewerInfoCall: ApolloQueryCall<ViewerQuery.Data>? = null

    fun getUserProfile() {
        getViewerInfoCall = NetworkClient.apolloClient
                .query(ViewerQuery.builder()
                        .build())

        getViewerInfoCall?.enqueue(object : ApolloCall.Callback<ViewerQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e, "get viewer info call error: ${e.message}")
            }

            override fun onResponse(response: Response<ViewerQuery.Data>) {
                Timber.d("get viewer info call success, resp = $response")
                val data = response.data()
                loginUserProfile.postValue(data)
            }

        })
    }

    override fun onCleared() {
        super.onCleared()

        if (getViewerInfoCall?.isCanceled == false) {
            getViewerInfoCall?.cancel()
        }
    }

}