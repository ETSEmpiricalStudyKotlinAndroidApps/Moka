package io.github.tonnyl.moka.ui.search.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.data.item.toNonNullSearchedOrganizationItem
import io.github.tonnyl.moka.data.item.toNonNullSearchedUserItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.querySearchUsers
import io.github.tonnyl.moka.queries.SearchUsersQuery
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
            val response = querySearchUsers(
                queryWords = keywords,
                first = params.requestedLoadSize
            )

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
                pageInfo.checkedStartCursor,
                pageInfo.checkedEndCursor
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
            val response = querySearchUsers(
                queryWords = keywords,
                first = params.requestedLoadSize,
                after = params.key
            )

            val list = mutableListOf<SearchedUserOrOrgItem>()
            val search = response.data()?.search

            search?.nodes?.forEach { node ->
                node?.let {
                    initSearchedUserOrOrgItemWithRawData(node)?.let {
                        list.add(it)
                    }
                }
            }

            callback.onResult(
                list,
                search?.pageInfo?.fragments?.pageInfo.checkedEndCursor
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
            val response = querySearchUsers(
                queryWords = keywords,
                first = params.requestedLoadSize,
                before = params.key
            )

            val list = mutableListOf<SearchedUserOrOrgItem>()
            val search = response.data()?.search

            search?.nodes?.forEach { node ->
                node?.let {
                    initSearchedUserOrOrgItemWithRawData(node)?.let {
                        list.add(it)
                    }
                }
            }

            callback.onResult(
                list,
                search?.pageInfo?.fragments?.pageInfo.checkedStartCursor
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

    private fun initSearchedUserOrOrgItemWithRawData(node: SearchUsersQuery.Node): SearchedUserOrOrgItem? {
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