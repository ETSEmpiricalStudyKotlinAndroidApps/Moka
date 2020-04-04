package io.github.tonnyl.moka.ui.search.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.data.item.toNonNullSearchedOrganizationItem
import io.github.tonnyl.moka.data.item.toNonNullSearchedUserItem
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.queries.SearchUserQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class SearchedUsersItemDataSource(
    var keywords: String,
    val initialLoadStatus: MutableLiveData<Resource<List<SearchedUserOrOrgItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource<List<SearchedUserOrOrgItem>>>
) : PageKeyedDataSource<String, SearchedUserOrOrgItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, SearchedUserOrOrgItem>
    ) {
        Timber.d("loadInitial keywords: $keywords")

        if (keywords.isEmpty()) {
            return
        }

        initialLoadStatus.postValue(Resource.loading(null))

        try {
            val userQuery = SearchUserQuery(
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

            val list = mutableListOf<SearchedUserOrOrgItem>()
            val search = response.data()?.search

            search?.nodes?.forEach { node ->
                node?.let {
                    initSearchedUserOrOrgItemWithRawData(node)?.let {
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
        callback: LoadCallback<String, SearchedUserOrOrgItem>
    ) {
        Timber.d("loadAfter keywords: $keywords")

        pagedLoadStatus.postValue(
            PagedResource(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val searchUserQuery = SearchUserQuery(
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

            val list = mutableListOf<SearchedUserOrOrgItem>()
            val search = response.data()?.search

            search?.nodes?.forEach { node ->
                node?.let {
                    initSearchedUserOrOrgItemWithRawData(node)?.let {
                        list.add(it)
                    }
                }
            }

            val pageInfo = search?.pageInfo?.fragments?.pageInfo

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
        callback: LoadCallback<String, SearchedUserOrOrgItem>
    ) {
        Timber.d("loadBefore keywords: $keywords")

        pagedLoadStatus.postValue(
            PagedResource(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val searchUserQuery = SearchUserQuery(
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

            val list = mutableListOf<SearchedUserOrOrgItem>()
            val search = response.data()?.search

            search?.nodes?.forEach { node ->
                node?.let {
                    initSearchedUserOrOrgItemWithRawData(node)?.let {
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
                }
            )

            retry = null

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

    private fun initSearchedUserOrOrgItemWithRawData(node: SearchUserQuery.Node): SearchedUserOrOrgItem? {
        return when {
            node.fragments.userListItemFragment != null -> {
                node.fragments.userListItemFragment.toNonNullSearchedUserItem()
            }
            node.fragments.organizationListItemFragment != null -> {
                node.fragments.organizationListItemFragment.toNonNullSearchedOrganizationItem()
            }
            else -> {
                null
            }
        }
    }

}