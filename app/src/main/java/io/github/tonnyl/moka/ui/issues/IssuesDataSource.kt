package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.github.tonnyl.moka.IssuesQuery
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.data.PagedResource
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.net.Resource
import timber.log.Timber

class IssuesDataSource(
        private val owner: String,
        private val name: String,
        private val loadStatusLiveData: MutableLiveData<PagedResource<List<IssueItem>>>
) : PageKeyedDataSource<String, IssueItem>() {

    var retry: (() -> Any)? = null

    private var apolloCall: ApolloQueryCall<IssuesQuery.Data>? = null

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, IssueItem>) {
        Timber.d("loadInitial")

        loadStatusLiveData.postValue(PagedResource(initial = Resource.loading(null)))

        val issuesQuery = IssuesQuery.builder()
                .owner(owner)
                .name(name)
                .perPage(params.requestedLoadSize)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(issuesQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<IssuesQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadInitial(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(initial = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<IssuesQuery.Data>) {
                val list = mutableListOf<IssueItem>()
                val repository = response.data()?.repository()

                repository?.issues()?.nodes()?.forEach { node ->
                    list.add(IssueItem.createFromRaw(node))
                }

                val pageInfo = repository?.issues()?.pageInfo()

                callback.onResult(
                        list,
                        if (pageInfo?.hasPreviousPage() == true) repository.issues().pageInfo().startCursor() else null,
                        if (pageInfo?.hasNextPage() == true) repository.issues().pageInfo().endCursor() else null
                )

                retry = null

                loadStatusLiveData.postValue(PagedResource(initial = Resource.success(list)))
            }

        })
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, IssueItem>) {
        Timber.d("loadAfter")

        loadStatusLiveData.postValue(PagedResource(after = Resource.loading(null)))

        val issuesQuery = IssuesQuery.builder()
                .owner(owner)
                .name(name)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(issuesQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<IssuesQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(after = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<IssuesQuery.Data>) {
                val list = mutableListOf<IssueItem>()
                val repository = response.data()?.repository()

                repository?.issues()?.nodes()?.forEach { node ->
                    list.add(IssueItem.createFromRaw(node))
                }

                callback.onResult(list, if (repository?.issues()?.pageInfo()?.hasNextPage() == true) repository.issues().pageInfo().endCursor() else null)

                retry = null

                loadStatusLiveData.postValue(PagedResource(after = Resource.success(list)))
            }

        })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, IssueItem>) {
        Timber.d("loadBefore")

        loadStatusLiveData.postValue(PagedResource(before = Resource.loading(null)))

        val issuesQuery = IssuesQuery.builder()
                .owner(owner)
                .name(name)
                .perPage(params.requestedLoadSize)
                .before(params.key)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(issuesQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<IssuesQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadBefore(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(before = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<IssuesQuery.Data>) {
                val list = mutableListOf<IssueItem>()
                val repository = response.data()?.repository()

                repository?.issues()?.nodes()?.forEach { node ->
                    list.add(IssueItem.createFromRaw(node))
                }

                callback.onResult(list, if (repository?.issues()?.pageInfo()?.hasPreviousPage() == true) repository.issues().pageInfo().startCursor() else null)

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