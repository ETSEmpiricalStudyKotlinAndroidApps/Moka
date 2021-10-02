package io.github.tonnyl.moka.ui.search.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullRepositoryItem
import io.github.tonnyl.moka.queries.SearchRepositoriesQuery
import io.github.tonnyl.moka.queries.SearchRepositoriesQuery.Data.Search.Node.Companion.repositoryListItemFragment
import io.github.tonnyl.moka.queries.SearchRepositoriesQuery.Data.Search.PageInfo.Companion.pageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class SearchedRepositoriesItemDataSource(
    private val apolloClient: ApolloClient,
    private val query: String
) : PagingSource<String, RepositoryItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RepositoryItem> {
        val list = mutableListOf<RepositoryItem>()

        return withContext(Dispatchers.IO) {
            try {
                val search = apolloClient.query(
                    query = SearchRepositoriesQuery(
                        queryWords = query,
                        first = params.loadSize,
                        last = null,
                        after = params.key,
                        before = params.key
                    )
                ).data?.search

                search?.nodes?.forEach { node ->
                    node?.let {
                        convertRawDataRepositoryItem(node)?.let {
                            list.add(it)
                        }
                    }
                }

                val pageInfo = search?.pageInfo?.pageInfo()
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

    override fun getRefreshKey(state: PagingState<String, RepositoryItem>): String? {
        return null
    }

    private fun convertRawDataRepositoryItem(
        node: SearchRepositoriesQuery.Data.Search.Node
    ): RepositoryItem? {
        return node.repositoryListItemFragment()?.toNonNullRepositoryItem()
    }

}