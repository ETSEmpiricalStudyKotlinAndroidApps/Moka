package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.IssuesQuery
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.net.Status
import timber.log.Timber

class IssuesDataSource(
        private val owner: String,
        private val name: String
) : PageKeyedDataSource<String, IssueItem>() {

    val status = MutableLiveData<Status>()

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, IssueItem>) {
        status.postValue(Status.LOADING)

        Timber.d("loadInitial")

        val issuesQuery = IssuesQuery.builder()
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

            val list = mutableListOf<IssueItem>()
            val repository = response.data()?.repository()

            repository?.issues()?.nodes()?.forEach { node ->
                list.add(IssueItem.createFromRaw(node))
            }

            callback.onResult(list, if (repository?.issues()?.pageInfo()?.hasPreviousPage() == true) repository.issues().pageInfo().startCursor() else null, if (repository?.issues()?.pageInfo()?.hasNextPage() == true) repository.issues().pageInfo().endCursor() else null)
        } catch (e: Exception) {
            status.postValue(Status.ERROR)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, IssueItem>) {
        Timber.d("loadAfter")

        val issuesQuery = IssuesQuery.builder()
                .owner(owner)
                .name(name)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

        NetworkClient.apolloClient
                .query(issuesQuery)
                .enqueue(object : ApolloCall.Callback<IssuesQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e, "loadAfter error: ${e.message}")
                    }

                    override fun onResponse(response: Response<IssuesQuery.Data>) {
                        val list = mutableListOf<IssueItem>()
                        val repository = response.data()?.repository()

                        repository?.issues()?.nodes()?.forEach { node ->
                            list.add(IssueItem.createFromRaw(node))
                        }

                        callback.onResult(list, if (repository?.issues()?.pageInfo()?.hasNextPage() == true) repository.issues().pageInfo().endCursor() else null)
                    }

                })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, IssueItem>) {
        Timber.d("loadBefore")

        val issuesQuery = IssuesQuery.builder()
                .owner(owner)
                .name(name)
                .perPage(params.requestedLoadSize)
                .before(params.key)
                .build()

        NetworkClient.apolloClient
                .query(issuesQuery)
                .enqueue(object : ApolloCall.Callback<IssuesQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e, "loadBefore error: ${e.message}")
                    }

                    override fun onResponse(response: Response<IssuesQuery.Data>) {
                        val list = mutableListOf<IssueItem>()
                        val repository = response.data()?.repository()

                        repository?.issues()?.nodes()?.forEach { node ->
                            list.add(IssueItem.createFromRaw(node))
                        }

                        callback.onResult(list, if (repository?.issues()?.pageInfo()?.hasPreviousPage() == true) repository.issues().pageInfo().startCursor() else null)
                    }

                })
    }

}