package io.github.tonnyl.moka.ui.issue

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.github.tonnyl.moka.IssueQuery
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.data.IssueGraphQL
import io.github.tonnyl.moka.network.Resource
import timber.log.Timber

class IssueLiveData(
        private val owner: String,
        private val name: String,
        private val number: Int
) : LiveData<Resource<IssueGraphQL?>>() {

    private var call: ApolloQueryCall<IssueQuery.Data>? = null

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
                .query(IssueQuery.builder()
                        .owner(owner)
                        .name(name)
                        .number(number)
                        .build())

        call?.enqueue(object : ApolloCall.Callback<IssueQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                postValue(Resource.error(e.message, null))
            }

            override fun onResponse(response: Response<IssueQuery.Data>) {
                val data = IssueGraphQL.createFromRaw(response.data()?.repository()?.issue())

                postValue(Resource.success(data))
            }

        })
    }

}