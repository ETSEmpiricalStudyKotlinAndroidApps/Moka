package io.github.tonnyl.moka.ui.profile

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.UserQuery
import io.github.tonnyl.moka.net.Resource
import timber.log.Timber

class UserProfileLiveData(
        private val login: String
) : LiveData<Resource<UserQuery.Data>>() {

    private var call: ApolloQueryCall<UserQuery.Data>? = null

    init {
        refresh()
    }

    override fun onInactive() {
        super.onInactive()
        if (call?.isCanceled == false) {
            call?.cancel()
        }
    }

    @MainThread
    fun refresh() {
        value = Resource.loading(null)

        call = NetworkClient.apolloClient
                .query(UserQuery.builder()
                        .login(login)
                        .build())

        call?.enqueue(object : ApolloCall.Callback<UserQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                postValue(Resource.error(e.message, null))
            }

            override fun onResponse(response: Response<UserQuery.Data>) {
                postValue(Resource.success(response.data()))
            }

        })
    }

}