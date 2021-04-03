package io.github.tonnyl.moka.ui.search.users

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.data.item.toNonNullSearchedOrganizationItem
import io.github.tonnyl.moka.data.toNonNullUserItem
import io.github.tonnyl.moka.network.queries.querySearchUsers
import io.github.tonnyl.moka.queries.SearchUsersQuery
import io.github.tonnyl.moka.queries.SearchUsersQuery.Data.Search.Nodes.Companion.organizationListItemFragment
import io.github.tonnyl.moka.queries.SearchUsersQuery.Data.Search.Nodes.Companion.userListItemFragment
import io.github.tonnyl.moka.queries.SearchUsersQuery.Data.Search.PageInfo.Companion.pageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SearchedUsersItemDataSource(
    private val query: String
) : PagingSource<String, SearchedUserOrOrgItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, SearchedUserOrOrgItem> {
        val list = mutableListOf<SearchedUserOrOrgItem>()
        return withContext(Dispatchers.IO) {
            try {
                val search = querySearchUsers(
                    queryWords = query,
                    first = params.loadSize,
                    after = params.key,
                    before = params.key
                ).data?.search

                list.addAll(
                    search?.nodes.orEmpty().mapNotNull { node ->
                        node?.let {
                            initSearchedUserOrOrgItemWithRawData(it)
                        }
                    }
                )

                val pageInfo = search?.pageInfo?.pageInfo()
                Page(
                    data = list,
                    prevKey = pageInfo.checkedStartCursor,
                    nextKey = pageInfo.checkedEndCursor
                )
            } catch (e: Exception) {
                Timber.e(e)

                Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<String, SearchedUserOrOrgItem>): String? {
        return null
    }

    private fun initSearchedUserOrOrgItemWithRawData(node: SearchUsersQuery.Data.Search.Nodes): SearchedUserOrOrgItem? {
        return node.userListItemFragment()?.toNonNullUserItem()?.let {
            SearchedUserOrOrgItem(user = it)
        } ?: node.organizationListItemFragment()?.toNonNullSearchedOrganizationItem()?.let {
            SearchedUserOrOrgItem(org = it)
        }
    }

}