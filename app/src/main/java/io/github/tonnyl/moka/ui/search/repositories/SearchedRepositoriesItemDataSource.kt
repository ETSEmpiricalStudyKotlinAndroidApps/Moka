package io.github.tonnyl.moka.ui.search.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem
import io.github.tonnyl.moka.data.item.toNonNullSearchedRepositoryItem
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.queries.SearchRepositoriesQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class SearchedRepositoriesItemDataSource(
    var keywords: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<SearchedRepositoryItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource<List<SearchedRepositoryItem>>>
) : PageKeyedDataSource<String, SearchedRepositoryItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, SearchedRepositoryItem>
    ) {
        Timber.d("loadInitial keywords: $keywords")

        if (keywords.isEmpty()) {
            return
        }

        initialLoadStatus.postValue(Resource.loading(null))

        try {
            val userQuery = SearchRepositoriesQuery(
                keywords,
                Input.fromNullable(params.requestedLoadSize),
                Input.absent(),
                Input.absent(),
                Input.absent()
            )

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(userQuery)
                    .execute()
            }

            val list = mutableListOf<SearchedRepositoryItem>()
            val search = response.data()?.search

            search?.nodes?.forEach { node ->
                node?.let {
                    convertRawDataToSearchedRepositoryItem(node)?.let {
                        list.add(it)
                    }
                }
            }

            val pageInfo = search?.pageInfo?.fragments?.pageInfo

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
        callback: LoadCallback<String, SearchedRepositoryItem>
    ) {
        Timber.d("loadAfter keywords: $keywords")

        pagedLoadStatus.postValue(
            PagedResource(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val searchUserQuery = SearchRepositoriesQuery(
                keywords,
                Input.fromNullable(params.requestedLoadSize),
                Input.absent(),
                Input.fromNullable(params.key),
                Input.absent()
            )

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(searchUserQuery)
                    .execute()
            }

            val list = mutableListOf<SearchedRepositoryItem>()
            val search = response.data()?.search

            search?.nodes?.forEach { node ->
                node?.let {
                    convertRawDataToSearchedRepositoryItem(node)?.let {
                        list.add(it)
                    }
                }
            }

            retry = null

            val pageInfo = search?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                if (pageInfo?.hasNextPage == true) {
                    pageInfo.endCursor
                } else {
                    null
                }
            )

            pagedLoadStatus.postValue(
                PagedResource(PagedResourceDirection.AFTER, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadAfter(params, callback)
            }

            pagedLoadStatus.postValue(
                PagedResource(PagedResourceDirection.AFTER, Resource.error(e.message, null))
            )
        }
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, SearchedRepositoryItem>
    ) {
        Timber.d("loadBefore keywords: $keywords")

        pagedLoadStatus.postValue(
            PagedResource(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val searchUserQuery = SearchRepositoriesQuery(
                keywords,
                Input.fromNullable(params.requestedLoadSize),
                Input.absent(),
                Input.absent(),
                Input.fromNullable(params.key)
            )

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(searchUserQuery)
                    .execute()
            }

            val list = mutableListOf<SearchedRepositoryItem>()
            val search = response.data()?.search

            search?.nodes?.forEach { node ->
                node?.let {
                    convertRawDataToSearchedRepositoryItem(node)?.let {
                        list.add(it)
                    }
                }
            }

            retry = null

            val pageInfo = search?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                if (pageInfo?.hasPreviousPage == true) {
                    pageInfo.startCursor
                } else {
                    null
                }
            )

            pagedLoadStatus.postValue(
                PagedResource(PagedResourceDirection.BEFORE, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadBefore(params, callback)
            }

            pagedLoadStatus.postValue(
                PagedResource(PagedResourceDirection.BEFORE, Resource.error(e.message, null))
            )
        }
    }

    private fun convertRawDataToSearchedRepositoryItem(node: SearchRepositoriesQuery.Node): SearchedRepositoryItem? {
        return node.fragments.repositoryListItemFragment?.toNonNullSearchedRepositoryItem()
    }

}