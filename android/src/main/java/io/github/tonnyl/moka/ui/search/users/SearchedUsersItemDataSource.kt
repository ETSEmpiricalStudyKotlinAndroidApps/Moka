package io.github.tonnyl.moka.ui.search.users

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.tonnyl.moka.common.data.SearchedUserOrOrgItem
import io.tonnyl.moka.common.data.extension.checkedEndCursor
import io.tonnyl.moka.common.data.extension.checkedStartCursor
import io.tonnyl.moka.graphql.SearchUsersQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class SearchedUsersItemDataSource(
    private val apolloClient: ApolloClient,
    private val query: String
) : PagingSource<String, SearchedUserOrOrgItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, SearchedUserOrOrgItem> {
        val list = mutableListOf<SearchedUserOrOrgItem>()
        return withContext(Dispatchers.IO) {
            try {
                val search = apolloClient.query(
                    query = SearchUsersQuery(
                        queryWords = query,
                        first = params.loadSize,
                        last = null,
                        after = params.key,
                        before = params.key
                    )
                ).execute().data?.search

                list.addAll(
                    search?.nodes.orEmpty().mapNotNull { node ->
                        node?.let {
                            initSearchedUserOrOrgItemWithRawData(it)
                        }
                    }
                )

                val pageInfo = search?.pageInfo?.pageInfo
                Page(
                    data = list,
                    prevKey = pageInfo.checkedStartCursor,
                    nextKey = pageInfo.checkedEndCursor
                )
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<String, SearchedUserOrOrgItem>): String? {
        return null
    }

    private fun initSearchedUserOrOrgItemWithRawData(node: SearchUsersQuery.Node): SearchedUserOrOrgItem? {
        return node.userListItemFragment?.let {
            SearchedUserOrOrgItem(user = it)
        } ?: node.organizationListItemFragment?.let {
            SearchedUserOrOrgItem(org = it)
        }
    }

}