package io.github.tonnyl.moka.ui.search.users

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.data.item.toNonNullSearchedOrganizationItem
import io.github.tonnyl.moka.data.toNonNullUserItem
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.SearchUsersQuery
import io.github.tonnyl.moka.queries.SearchUsersQuery.Data.Search.Node.Companion.organizationListItemFragment
import io.github.tonnyl.moka.queries.SearchUsersQuery.Data.Search.Node.Companion.userListItemFragment
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
                val search = GraphQLClient.apolloClient.query(
                    query = SearchUsersQuery(
                        queryWords = query,
                        first = Input.Present(value = params.loadSize),
                        last = Input.Present(value = null),
                        after = Input.Present(value = params.key),
                        before = Input.Present(value = params.key)
                    )
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

    private fun initSearchedUserOrOrgItemWithRawData(node: SearchUsersQuery.Data.Search.Node): SearchedUserOrOrgItem? {
        return node.userListItemFragment()?.toNonNullUserItem()?.let {
            SearchedUserOrOrgItem(user = it)
        } ?: node.organizationListItemFragment()?.toNonNullSearchedOrganizationItem()?.let {
            SearchedUserOrOrgItem(org = it)
        }
    }

}