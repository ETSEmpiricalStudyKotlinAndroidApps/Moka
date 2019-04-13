package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.PullRequestsQuery
import io.github.tonnyl.moka.data.PagedResource
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.net.Resource
import timber.log.Timber

class PullRequestsDataSource(
        private val owner: String,
        private val name: String,
        private val loadStatusLiveData: MutableLiveData<PagedResource<List<PullRequestItem>>>
) : PageKeyedDataSource<String, PullRequestItem>() {

    var retry: (() -> Any)? = null

    private var apolloCall: ApolloQueryCall<PullRequestsQuery.Data>? = null

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, PullRequestItem>) {
        Timber.d("loadInitial")

        loadStatusLiveData.postValue(PagedResource(initial = Resource.loading(null)))

        val issuesQuery = PullRequestsQuery.builder()
                .owner(owner)
                .name(name)
                .perPage(params.requestedLoadSize)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(issuesQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<PullRequestsQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadInitial(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(initial = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<PullRequestsQuery.Data>) {
                val list = mutableListOf<PullRequestItem>()
                val repository = response.data()?.repository()

                repository?.pullRequests()?.nodes()?.forEach { node ->
                    list.add(PullRequestItem.createFromRaw(node))
                }

                val pageInfo = repository?.pullRequests()?.pageInfo()

                callback.onResult(
                        list,
                        if (pageInfo?.hasPreviousPage() == true) repository.pullRequests().pageInfo().startCursor() else null,
                        if (pageInfo?.hasNextPage() == true) repository.pullRequests().pageInfo().endCursor() else null
                )

                retry = null

                loadStatusLiveData.postValue(PagedResource(initial = Resource.success(list)))
            }

        })
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, PullRequestItem>) {
        Timber.d("loadAfter")

        loadStatusLiveData.postValue(PagedResource(after = Resource.loading(null)))

        val issuesQuery = PullRequestsQuery.builder()
                .owner(owner)
                .name(name)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(issuesQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<PullRequestsQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(after = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<PullRequestsQuery.Data>) {
                val list = mutableListOf<PullRequestItem>()
                val repository = response.data()?.repository()

                repository?.pullRequests()?.nodes()?.forEach { node ->
                    list.add(PullRequestItem.createFromRaw(node))
                }

                callback.onResult(list, if (repository?.pullRequests()?.pageInfo()?.hasNextPage() == true) repository.pullRequests().pageInfo().endCursor() else null)

                retry = null

                loadStatusLiveData.postValue(PagedResource(after = Resource.success(list)))
            }

        })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, PullRequestItem>) {
        Timber.d("loadBefore")

        loadStatusLiveData.postValue(PagedResource(before = Resource.loading(null)))

        val issuesQuery = PullRequestsQuery.builder()
                .owner(owner)
                .name(name)
                .perPage(params.requestedLoadSize)
                .before(params.key)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(issuesQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<PullRequestsQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadBefore(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(before = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<PullRequestsQuery.Data>) {
                val list = mutableListOf<PullRequestItem>()
                val repository = response.data()?.repository()

                repository?.pullRequests()?.nodes()?.forEach { node ->
                    list.add(PullRequestItem.createFromRaw(node))
                }

                callback.onResult(list, if (repository?.pullRequests()?.pageInfo()?.hasPreviousPage() == true) repository.pullRequests().pageInfo().startCursor() else null)

                retry = null

                loadStatusLiveData.postValue(PagedResource(before = Resource.success(list)))
            }

        })
    }

    override fun invalidate() {
        super.invalidate()

        if (apolloCall?.isCanceled == false) {
            apolloCall?.cancel()
        }
    }

}