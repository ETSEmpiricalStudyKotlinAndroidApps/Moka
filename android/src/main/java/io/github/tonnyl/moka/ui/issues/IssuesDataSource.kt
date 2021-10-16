package io.github.tonnyl.moka.ui.issues

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.data.item.toNonNullIssueItem
import io.tonnyl.moka.graphql.IssuesQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class IssuesDataSource(
    private val apolloClient: ApolloClient,
    private val owner: String,
    private val name: String
) : PagingSource<String, IssueItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, IssueItem> {
        val list = mutableListOf<IssueItem>()
        return withContext(Dispatchers.IO) {
            try {
                val repository = apolloClient.query(
                    query = IssuesQuery(
                        owner = owner,
                        name = name,
                        after = params.key,
                        before = params.key,
                        perPage = params.loadSize
                    )
                ).data?.repository

                repository?.issues?.nodes?.forEach { node ->
                    node?.let {
                        list.add(node.toNonNullIssueItem())
                    }
                }

                val pageInfo = repository?.issues?.pageInfo?.pageInfo

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

    override fun getRefreshKey(state: PagingState<String, IssueItem>): String? {
        return null
    }

}