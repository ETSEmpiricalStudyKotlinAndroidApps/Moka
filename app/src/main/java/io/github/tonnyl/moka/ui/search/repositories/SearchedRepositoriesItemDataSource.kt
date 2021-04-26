package io.github.tonnyl.moka.ui.search.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullRepositoryItem
import io.github.tonnyl.moka.queries.SearchRepositoriesQuery
import io.github.tonnyl.moka.queries.SearchRepositoriesQuery.Data.Search.Node.Companion.repositoryListItemFragment
import io.github.tonnyl.moka.queries.SearchRepositoriesQuery.Data.Search.PageInfo.Companion.pageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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
                        first = Input.Present(value = params.loadSize),
                        last = Input.Present(value = null),
                        after = Input.Present(value = params.key),
                        before = Input.Present(value = params.key)
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
                Timber.e(e)

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