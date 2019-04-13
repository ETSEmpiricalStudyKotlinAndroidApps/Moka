package io.github.tonnyl.moka.ui.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.RepositoryQuery
import io.github.tonnyl.moka.data.RepositoryGraphQL
import io.github.tonnyl.moka.net.Resource
import timber.log.Timber

class RepositoryLiveData(
        private val login: String,
        private val name: String
) : LiveData<Resource<RepositoryGraphQL>>() {

    private var call: ApolloQueryCall<RepositoryQuery.Data>? = null

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
                .query(RepositoryQuery.builder()
                        .login(login)
                        .repoName(name)
                        .build())

        call?.enqueue(object : ApolloCall.Callback<RepositoryQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                postValue(Resource.error(e.message, null))
            }

            override fun onResponse(response: Response<RepositoryQuery.Data>) {
                postValue(Resource.success(RepositoryGraphQL.createFromRaw(response.data())))
            }

        })
    }

}