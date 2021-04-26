package io.github.tonnyl.moka.ui.prs

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.data.item.toNonNullPullRequestItem
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.PullRequestsQuery
import io.github.tonnyl.moka.queries.PullRequestsQuery.Data.Repository.PullRequests.PageInfo.Companion.pageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PullRequestsDataSource(
    private val owner: String,
    private val name: String
) : PagingSource<String, PullRequestItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PullRequestItem> {
        val list = mutableListOf<PullRequestItem>()
        return withContext(Dispatchers.IO) {
            try {
                val repository = GraphQLClient.apolloClient.query(
                    query = PullRequestsQuery(
                        owner = owner,
                        name = name,
                        after = Input.Present(value = params.key),
                        before = Input.Present(value = params.key),
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
                Timber.e(e)

                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<String, PullRequestItem>): String? {
        return null
    }

}