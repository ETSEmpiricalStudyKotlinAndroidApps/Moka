package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.PullRequestsQuery
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.net.Status
import timber.log.Timber

class PullRequestsDataSource(
        private val owner: String,
        private val name: String
) : PageKeyedDataSource<String, PullRequestItem>() {

    val status = MutableLiveData<Status>()

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, PullRequestItem>) {
        status.postValue(Status.LOADING)

        Timber.d("loadInitial")

        val issuesQuery = PullRequestsQuery.builder()
                .owner(owner)
                .name(name)
                .perPage(params.requestedLoadSize)
                .build()

        val call = NetworkClient.apolloClient
                .query(issuesQuery)

        try {
            // triggered by a refresh, we better execute sync
            val response = Rx2Apollo.from(call).blockingFirst()

            status.postValue(if (response.hasErrors().not()) Status.ERROR else Status.SUCCESS)

            val list = mutableListOf<PullRequestItem>()
            val repository = response.data()?.repository()

            repository?.pullRequests()?.nodes()?.forEach { node ->
                list.add(PullRequestItem.createFromRaw(node))
            }

            callback.onResult(list, if (repository?.pullRequests()?.pageInfo()?.hasPreviousPage() == true) repository.pullRequests().pageInfo().startCursor() else null, if (repository?.pullRequests()?.pageInfo()?.hasNextPage() == true) repository.pullRequests().pageInfo().endCursor() else null)
        } catch (e: Exception) {
            status.postValue(Status.ERROR)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, PullRequestItem>) {
        Timber.d("loadAfter")

        val issuesQuery = PullRequestsQuery.builder()
                .owner(owner)
                .name(name)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

        NetworkClient.apolloClient
                .query(issuesQuery)
                .enqueue(object : ApolloCall.Callback<PullRequestsQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e, "loadAfter error: ${e.message}")
                    }

                    override fun onResponse(response: Response<PullRequestsQuery.Data>) {
                        val list = mutableListOf<PullRequestItem>()
                        val repository = response.data()?.repository()

                        repository?.pullRequests()?.nodes()?.forEach { node ->
                            list.add(PullRequestItem.createFromRaw(node))
                        }

                        callback.onResult(list, if (repository?.pullRequests()?.pageInfo()?.hasNextPage() == true) repository.pullRequests().pageInfo().endCursor() else null)
                    }

                })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, PullRequestItem>) {
        Timber.d("loadBefore")

        val issuesQuery = PullRequestsQuery.builder()
                .owner(owner)
                .name(name)
                .perPage(params.requestedLoadSize)
                .before(params.key)
                .build()

        NetworkClient.apolloClient
                .query(issuesQuery)
                .enqueue(object : ApolloCall.Callback<PullRequestsQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e, "loadBefore error: ${e.message}")
                    }

                    override fun onResponse(response: Response<PullRequestsQuery.Data>) {
                        val list = mutableListOf<PullRequestItem>()
                        val repository = response.data()?.repository()

                        repository?.pullRequests()?.nodes()?.forEach { node ->
                            list.add(PullRequestItem.createFromRaw(node))
                        }

                        callback.onResult(list, if (repository?.pullRequests()?.pageInfo()?.hasPreviousPage() == true) repository.pullRequests().pageInfo().startCursor() else null)
                    }

                })
    }

}