package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.OwnedRepositoriesQuery
import io.github.tonnyl.moka.data.PagedResource
import io.github.tonnyl.moka.data.RepositoryAbstract
import io.github.tonnyl.moka.net.Resource
import timber.log.Timber

class OwnedRepositoriesDataSource(
        private val login: String,
        private val loadStatusLiveData: MutableLiveData<PagedResource<List<RepositoryAbstract>>>
) : PageKeyedDataSource<String, RepositoryAbstract>() {

    var retry: (() -> Any)? = null

    private var apolloCall: ApolloQueryCall<OwnedRepositoriesQuery.Data>? = null

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, RepositoryAbstract>) {
        Timber.d("loadInitial")

        loadStatusLiveData.postValue(PagedResource(initial = Resource.loading(null)))

        val repositoriesQuery = OwnedRepositoriesQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(repositoriesQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<OwnedRepositoriesQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadInitial(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(initial = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<OwnedRepositoriesQuery.Data>) {
                val list = mutableListOf<RepositoryAbstract>()
                val user = response.data()?.user()

                user?.repositories()?.nodes()?.forEach { node ->
                    list.add(RepositoryAbstract.createFromOwnedRepositoryDataNode(node))
                }

                val pageInfo = user?.repositories()?.pageInfo()

                callback.onResult(
                        list,
                        if (pageInfo?.hasPreviousPage() == true) user.repositories().pageInfo().startCursor() else null,
                        if (pageInfo?.hasNextPage() == true) user.repositories().pageInfo().endCursor() else null
                )

                retry = null

                loadStatusLiveData.postValue(PagedResource(initial = Resource.success(list)))
            }

        })
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, RepositoryAbstract>) {
        Timber.d("loadAfter")

        loadStatusLiveData.postValue(PagedResource(after = Resource.loading(null)))

        val repositoriesQuery = OwnedRepositoriesQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(repositoriesQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<OwnedRepositoriesQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(after = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<OwnedRepositoriesQuery.Data>) {
                val list = mutableListOf<RepositoryAbstract>()
                val user = response.data()?.user()

                user?.repositories()?.nodes()?.forEach { node ->
                    list.add(RepositoryAbstract.createFromOwnedRepositoryDataNode(node))
                }

                callback.onResult(list, if (user?.repositories()?.pageInfo()?.hasNextPage() == true) user.repositories().pageInfo().endCursor() else null)

                retry = null

                loadStatusLiveData.postValue(PagedResource(after = Resource.success(list)))
            }

        })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, RepositoryAbstract>) {
        Timber.d("loadBefore")

        loadStatusLiveData.postValue(PagedResource(before = Resource.loading(null)))

        val repositoriesQuery = OwnedRepositoriesQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .before(params.key)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(repositoriesQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<OwnedRepositoriesQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadBefore(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(before = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<OwnedRepositoriesQuery.Data>) {
                val list = mutableListOf<RepositoryAbstract>()
                val user = response.data()?.user()

                user?.repositories()?.nodes()?.forEach { node ->
                    list.add(RepositoryAbstract.createFromOwnedRepositoryDataNode(node))
                }

                callback.onResult(list, if (user?.repositories()?.pageInfo()?.hasPreviousPage() == true) user.repositories().pageInfo().startCursor() else null)

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