package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.StarredRepositoriesQuery
import io.github.tonnyl.moka.data.RepositoryAbstract
import io.github.tonnyl.moka.data.Status
import timber.log.Timber

class StarredRepositoriesDataSource(private val login: String) : PageKeyedDataSource<String, RepositoryAbstract>() {

    val status = MutableLiveData<Status>()

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, RepositoryAbstract>) {
        status.postValue(Status.LOADING)

        Timber.d("loadInitial")

        val repositoriesQuery = StarredRepositoriesQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .build()

        val call = NetworkClient.apolloClient
                .query(repositoriesQuery)

        try {
            // triggered by a refresh, we better execute sync
            val response = Rx2Apollo.from(call).blockingFirst()

            status.postValue(if (response.hasErrors().not()) Status.ERROR else Status.SUCCESS)

            val list = mutableListOf<RepositoryAbstract>()
            val user = response.data()?.user()

            user?.starredRepositories()?.nodes()?.forEach { node ->
                list.add(RepositoryAbstract.createFromStarredRepositoryDataNode(node))
            }

            callback.onResult(list, if (user?.starredRepositories()?.pageInfo()?.hasPreviousPage() == true) user.starredRepositories().pageInfo().startCursor() else null, if (user?.starredRepositories()?.pageInfo()?.hasNextPage() == true) user.starredRepositories().pageInfo().endCursor() else null)
        } catch (e: Exception) {
            status.postValue(Status.ERROR)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, RepositoryAbstract>) {
        Timber.d("loadAfter")

        val repositoriesQuery = StarredRepositoriesQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

        NetworkClient.apolloClient
                .query(repositoriesQuery)
                .enqueue(object : ApolloCall.Callback<StarredRepositoriesQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e, "loadAfter error: ${e.message}")
                    }

                    override fun onResponse(response: Response<StarredRepositoriesQuery.Data>) {
                        val list = mutableListOf<RepositoryAbstract>()
                        val user = response.data()?.user()

                        user?.starredRepositories()?.nodes()?.forEach { node ->
                            list.add(RepositoryAbstract.createFromStarredRepositoryDataNode(node))
                        }

                        callback.onResult(list, if (user?.starredRepositories()?.pageInfo()?.hasNextPage() == true) user.starredRepositories().pageInfo().endCursor() else null)
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

        NetworkClient.apolloClient
                .query(repositoriesQuery)
                .enqueue(object : ApolloCall.Callback<StarredRepositoriesQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e, "loadBefore error: ${e.message}")
                    }

                    override fun onResponse(response: Response<StarredRepositoriesQuery.Data>) {
                        val list = mutableListOf<RepositoryAbstract>()
                        val user = response.data()?.user()

                        user?.starredRepositories()?.nodes()?.forEach { node ->
                            list.add(RepositoryAbstract.createFromStarredRepositoryDataNode(node))
                        }

                        callback.onResult(list, if (user?.starredRepositories()?.pageInfo()?.hasPreviousPage() == true) user.starredRepositories().pageInfo().startCursor() else null)
                    }

                })
    }

}