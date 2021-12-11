package io.github.tonnyl.moka.ui.search.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import io.tonnyl.moka.common.data.extension.checkedEndCursor
import io.tonnyl.moka.common.data.extension.checkedStartCursor
import io.tonnyl.moka.graphql.SearchRepositoriesQuery
import io.tonnyl.moka.graphql.fragment.RepositoryListItemFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class SearchedRepositoriesItemDataSource(
    private val apolloClient: ApolloClient,
    private val query: String
) : PagingSource<String, RepositoryListItemFragment>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RepositoryListItemFragment> {
        val list = mutableListOf<RepositoryListItemFragment>()

        return withContext(Dispatchers.IO) {
            try {
                val search = apolloClient.query(
                    query = SearchRepositoriesQuery(
                        queryWords = query,
                        first = Optional.presentIfNotNull(params.loadSize),
                        last = Optional.Absent,
                        after = Optional.presentIfNotNull(params.key),
                        before = Optional.presentIfNotNull(params.key)
                    )
                ).execute().data?.search

                search?.nodes?.forEach { node ->
                    node?.repositoryListItemFragment?.let {
                        list.add(it)
                    }
                }

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

    override fun getRefreshKey(state: PagingState<String, RepositoryListItemFragment>): String? {
        return null
    }

}