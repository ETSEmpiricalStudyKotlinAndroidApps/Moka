package io.github.tonnyl.moka.ui.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullRepositoryItem
import io.github.tonnyl.moka.queries.OwnedRepositoriesQuery
import io.github.tonnyl.moka.queries.OwnedRepositoriesQuery.Data.User.Repositories.Node.Companion.repositoryListItemFragment
import io.github.tonnyl.moka.queries.OwnedRepositoriesQuery.Data.User.Repositories.PageInfo.Companion.pageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class OwnedRepositoriesDataSource(
    private val apolloClient: ApolloClient,
    private val login: String
) : PagingSource<String, RepositoryItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RepositoryItem> {
        val list = mutableListOf<RepositoryItem>()
        return withContext(Dispatchers.IO) {
            try {
                val user = apolloClient.query(
                    query = OwnedRepositoriesQuery(
                        login = login,
                        perPage = params.loadSize,
                        after = params.key,
                        before = params.key
                    )
                ).data?.user

                list.addAll(
                    user?.repositories?.nodes.orEmpty().mapNotNull { node ->
                        node?.repositoryListItemFragment()?.toNonNullRepositoryItem()
                    }
                )

                val pageInfo = user?.repositories?.pageInfo?.pageInfo()

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

}