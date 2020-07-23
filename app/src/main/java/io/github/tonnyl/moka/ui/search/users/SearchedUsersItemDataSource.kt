package io.github.tonnyl.moka.ui.search.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams.Refresh
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.data.item.toNonNullSearchedOrganizationItem
import io.github.tonnyl.moka.data.item.toNonNullSearchedUserItem
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.querySearchUsers
import io.github.tonnyl.moka.queries.SearchUsersQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SearchedUsersItemDataSource(
    val query: String,
    val initialLoadStatus: MutableLiveData<Resource<List<SearchedUserOrOrgItem>>>
) : PagingSource<String, SearchedUserOrOrgItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, SearchedUserOrOrgItem> {
        val list = mutableListOf<SearchedUserOrOrgItem>()
        return withContext(Dispatchers.IO) {
            try {
                if (params is Refresh) {
                    initialLoadStatus.postValue(Resource.loading(null))
                }

                val search = querySearchUsers(
                    queryWords = query,
                    first = params.loadSize,
                    after = params.key,
                    before = params.key
                ).data()?.search

                search?.nodes?.forEach { node ->
                    node?.let {
                        initSearchedUserOrOrgItemWithRawData(node)?.let {
                            list.add(it)
                        }
                    }
                }

                val pageInfo = search?.pageInfo?.fragments?.pageInfo
                Page(
                    data = list,
                    prevKey = pageInfo.checkedStartCursor,
                    nextKey = pageInfo.checkedEndCursor
                ).also {
                    if (params is Refresh) {
                        initialLoadStatus.postValue(Resource.success(list))
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)

                if (params is Refresh) {
                    initialLoadStatus.postValue(Resource.error(e.message, null))
                }

                Error<String, SearchedUserOrOrgItem>(e)
            }
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