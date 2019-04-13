package io.github.tonnyl.moka.ui.pr

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.PullRequestQuery
import io.github.tonnyl.moka.data.PullRequestGraphQL
import io.github.tonnyl.moka.net.Resource
import timber.log.Timber

class PullRequestLiveData(
        private val owner: String,
        private val name: String,
        private val number: Int
) : LiveData<Resource<PullRequestGraphQL?>>() {

    private var call: ApolloQueryCall<PullRequestQuery.Data>? = null

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
                .query(PullRequestQuery.builder()
                        .owner(owner)
                        .name(name)
                        .number(number)
                        .build())

        call?.enqueue(object : ApolloCall.Callback<PullRequestQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                postValue(Resource.error(e.message, null))
            }

            override fun onResponse(response: Response<PullRequestQuery.Data>) {
                Timber.d("response: $response")

                val data = PullRequestGraphQL.createFromRaw(response.data()?.repository()?.pullRequest())

                postValue(Resource.success(data))
            }

        })
    }

}