package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.StarredRepositoriesQuery
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.data.RepositoryAbstract
import io.github.tonnyl.moka.network.Resource
import timber.log.Timber

class StarredRepositoriesDataSource(
        private val login: String,
        private val loadStatusLiveData: MutableLiveData<PagedResource<List<RepositoryAbstract>>>
) : PageKeyedDataSource<String, RepositoryAbstract>() {

    var retry: (() -> Any)? = null

    private var apolloCall: ApolloQueryCall<StarredRepositoriesQuery.Data>? = null

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, RepositoryAbstract>) {
        Timber.d("loadInitial")

        loadStatusLiveData.postValue(PagedResource(initial = Resource.loading(null)))

        val repositoriesQuery = StarredRepositoriesQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(repositoriesQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<StarredRepositoriesQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadInitial(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(initial = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<StarredRepositoriesQuery.Data>) {
                val list = mutableListOf<RepositoryAbstract>()
                val user = response.data()?.user()

                user?.starredRepositories()?.nodes()?.forEach { node ->
                    list.add(RepositoryAbstract.createFromStarredRepositoryDataNode(node))
                }

                val pageInfo = user?.starredRepositories()?.pageInfo()

                callback.onResult(
                        list,
                        if (pageInfo?.hasPreviousPage() == true) user.starredRepositories().pageInfo().startCursor() else null,
                        if (pageInfo?.hasNextPage() == true) user.starredRepositories().pageInfo().endCursor() else null
                )

                retry = null

                loadStatusLiveData.postValue(PagedResource(initial = Resource.success(list)))
            }

        })
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, RepositoryAbstract>) {
        Timber.d("loadAfter")

        loadStatusLiveData.postValue(PagedResource(after = Resource.loading(null)))

        val repositoriesQuery = StarredRepositoriesQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(repositoriesQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<StarredRepositoriesQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(after = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<StarredRepositoriesQuery.Data>) {
                val list = mutableListOf<RepositoryAbstract>()
                val user = response.data()?.user()

                user?.starredRepositories()?.nodes()?.forEach { node ->
                    list.add(RepositoryAbstract.createFromStarredRepositoryDataNode(node))
                }

                callback.onResult(list, if (user?.starredRepositories()?.pageInfo()?.hasNextPage() == true) user.starredRepositories().pageInfo().endCursor() else null)

                retry = null

                loadStatusLiveData.postValue(PagedResource(after = Resource.success(list)))
            }

        })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, RepositoryAbstract>) {
        Timber.d("loadBefore")

        val repositoriesQuery = StarredRepositoriesQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .before(params.key)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(repositoriesQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<StarredRepositoriesQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadBefore(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(before = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<StarredRepositoriesQuery.Data>) {
                val list = mutableListOf<RepositoryAbstract>()
                val user = response.data()?.user()

                user?.starredRepositories()?.nodes()?.forEach { node ->
                    list.add(RepositoryAbstract.createFromStarredRepositoryDataNode(node))
                }

                callback.onResult(list, if (user?.starredRepositories()?.pageInfo()?.hasPreviousPage() == true) user.starredRepositories().pageInfo().startCursor() else null)

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