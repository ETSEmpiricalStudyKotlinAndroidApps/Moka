package io.github.tonnyl.moka.ui.prs

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.data.item.toNonNullPullRequestItem
import io.github.tonnyl.moka.queries.PullRequestsQuery
import io.github.tonnyl.moka.queries.PullRequestsQuery.Data.Repository.PullRequests.PageInfo.Companion.pageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class PullRequestsDataSource(
    private val apolloClient: ApolloClient,
    private val owner: String,
    private val name: String
) : PagingSource<String, PullRequestItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PullRequestItem> {
        val list = mutableListOf<PullRequestItem>()
        return withContext(Dispatchers.IO) {
            try {
                val repository = apolloClient.query(
                    query = PullRequestsQuery(
                        owner = owner,
                        name = name,
                        after = params.key,
                        before = params.key,
                        perPage = params.loadSize
                    )
                ).data?.repository

                list.addAll(
                    repository?.pullRequests?.nodes.orEmpty().mapNotNull { node ->
                        node?.toNonNullPullRequestItem()
                    }
                )

                val pageInfo = repository?.pullRequests?.pageInfo?.pageInfo()

                LoadResult.Page(
                    data = list,
                    prevKey = pageInfo.checkedStartCursor,
                    nextKey = pageInfo.checkedEndCursor
                )
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<String, PullRequestItem>): String? {
        return null
    }

}