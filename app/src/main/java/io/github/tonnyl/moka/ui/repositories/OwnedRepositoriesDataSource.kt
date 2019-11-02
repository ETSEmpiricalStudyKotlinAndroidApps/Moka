package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.OwnedRepositoriesQuery
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.data.toNonNullRepositoryItem
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class OwnedRepositoriesDataSource(
    private val login: String,
    private val loadStatusLiveData: MutableLiveData<Resource<List<RepositoryItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource2<List<RepositoryItem>>>
) : PageKeyedDataSource<String, RepositoryItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, RepositoryItem>
    ) {
        Timber.d("loadInitial")

        loadStatusLiveData.postValue(Resource.loading(null))

        try {
            val repositoriesQuery = OwnedRepositoriesQuery(
                login,
                params.requestedLoadSize,
                Input.absent(),
                Input.absent()
            )

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(repositoriesQuery)
                    .execute()
            }

            val list = mutableListOf<RepositoryItem>()
            val user = response.data()?.user

            user?.repositories?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.fragments.repositoryListItemFragment.toNonNullRepositoryItem())
                }
            }

            val pageInfo = user?.repositories?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                if (pageInfo?.hasPreviousPage == true) {
                    pageInfo.startCursor
                } else {
                    null
                },
                if (pageInfo?.hasNextPage == true) {
                    pageInfo.endCursor
                } else {
                    null
                }
            )

            retry = null

            loadStatusLiveData.postValue(Resource.success(list))
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadInitial(params, callback)
            }

            loadStatusLiveData.postValue(Resource.error(e.message, null))
        }
    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, RepositoryItem>
    ) {
        Timber.d("loadAfter")

        pagedLoadStatus.postValue(
            PagedResource2(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val repositoriesQuery = OwnedRepositoriesQuery(
                login,
                params.requestedLoadSize,
                Input.fromNullable(params.key),
                Input.absent()
            )

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(repositoriesQuery)
                    .execute()
            }

            val list = mutableListOf<RepositoryItem>()
            val user = response.data()?.user

            user?.repositories?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.fragments.repositoryListItemFragment.toNonNullRepositoryItem())
                }
            }

            val pageInfo = user?.repositories?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                if (pageInfo?.hasNextPage == true) {
                    pageInfo.endCursor
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
        callback: LoadCallback<String, RepositoryItem>
    ) {
        Timber.d("loadBefore")

        pagedLoadStatus.postValue(
            PagedResource2(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val repositoriesQuery = OwnedRepositoriesQuery(
                login,
                params.requestedLoadSize,
                Input.absent(),
                Input.fromNullable(params.key)
            )

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(repositoriesQuery)
                    .execute()
            }

            val list = mutableListOf<RepositoryItem>()
            val user = response.data()?.user

            user?.repositories?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.fragments.repositoryListItemFragment.toNonNullRepositoryItem())
                }
            }

            val pageInfo = user?.repositories?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                if (pageInfo?.hasPreviousPage == true) {
                    pageInfo.startCursor
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