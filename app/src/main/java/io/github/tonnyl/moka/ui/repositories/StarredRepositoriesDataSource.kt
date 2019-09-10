package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.StarredRepositoriesQuery
import io.github.tonnyl.moka.data.RepositoryAbstract
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class StarredRepositoriesDataSource(
    private val login: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<RepositoryAbstract>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource2<List<RepositoryAbstract>>>
) : PageKeyedDataSource<String, RepositoryAbstract>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, RepositoryAbstract>
    ) {
        Timber.d("loadInitial")

        initialLoadStatus.postValue(Resource.loading(null))

        try {
            val repositoriesQuery = StarredRepositoriesQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .build()

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(repositoriesQuery)
                    .execute()
            }

            val list = mutableListOf<RepositoryAbstract>()
            val user = response.data()?.user()

            user?.starredRepositories()?.nodes()?.forEach { node ->
                list.add(RepositoryAbstract.createFromStarredRepositoryDataNode(node))
            }

            val pageInfo = user?.starredRepositories()?.pageInfo()

            callback.onResult(
                list,
                if (pageInfo?.hasPreviousPage() == true) {
                    user.starredRepositories().pageInfo().startCursor()
                } else {
                    null
                },
                if (pageInfo?.hasNextPage() == true) {
                    user.starredRepositories().pageInfo().endCursor()
                } else {
                    null
                }
            )

            retry = null

            initialLoadStatus.postValue(Resource.success(list))
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadInitial(params, callback)
            }

            initialLoadStatus.postValue(Resource.error(e.message, null))
        }
    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, RepositoryAbstract>
    ) {
        Timber.d("loadAfter")

        pagedLoadStatus.postValue(
            PagedResource2(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val repositoriesQuery = StarredRepositoriesQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(repositoriesQuery)
                    .execute()
            }

            val list = mutableListOf<RepositoryAbstract>()
            val user = response.data()?.user()

            user?.starredRepositories()?.nodes()?.forEach { node ->
                list.add(RepositoryAbstract.createFromStarredRepositoryDataNode(node))
            }

            callback.onResult(
                list,
                if (user?.starredRepositories()?.pageInfo()?.hasNextPage() == true) {
                    user.starredRepositories().pageInfo().endCursor()
                } else {
                    null
                }
            )

            retry = null

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.AFTER, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadAfter(params, callback)
            }

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.AFTER, Resource.error(e.message, null))
            )
        }
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, RepositoryAbstract>
    ) {
        Timber.d("loadBefore")

        pagedLoadStatus.postValue(
            PagedResource2(PagedResourceDirection.BEFORE, Resource.loading(null))
        )
        try {
            val repositoriesQuery = StarredRepositoriesQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .before(params.key)
                .build()

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(repositoriesQuery)
                    .execute()
            }

            val list = mutableListOf<RepositoryAbstract>()
            val user = response.data()?.user()

            user?.starredRepositories()?.nodes()?.forEach { node ->
                list.add(RepositoryAbstract.createFromStarredRepositoryDataNode(node))
            }

            callback.onResult(
                list,
                if (user?.starredRepositories()?.pageInfo()?.hasPreviousPage() == true) {
                    user.starredRepositories().pageInfo().startCursor()
                } else {
                    null
                }
            )

            retry = null

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.BEFORE, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadBefore(params, callback)
            }

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.BEFORE, Resource.error(e.message, null))
            )
        }
    }

}