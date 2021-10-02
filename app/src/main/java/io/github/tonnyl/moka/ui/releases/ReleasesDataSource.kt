package io.github.tonnyl.moka.ui.releases

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.fragment.ReleaseListItem
import io.github.tonnyl.moka.queries.RepositoryReleasesQuery
import io.github.tonnyl.moka.queries.RepositoryReleasesQuery.Data.Repository.Releases.PageInfo.Companion.pageInfo
import io.github.tonnyl.moka.type.OrderDirection
import io.github.tonnyl.moka.type.ReleaseOrder
import io.github.tonnyl.moka.type.ReleaseOrderField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class ReleasesDataSource(
    private val apolloClient: ApolloClient,
    private val owner: String,
    private val name: String
) : PagingSource<String, ReleaseListItem>() {

    override fun getRefreshKey(state: PagingState<String, ReleaseListItem>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, ReleaseListItem> {
        val list = mutableListOf<ReleaseListItem>()
        return withContext(Dispatchers.IO) {
            try {
                val repository = apolloClient.query(
                    query = RepositoryReleasesQuery(
                        login = owner,
                        repoName = name,
                        after = params.key,
                        before = params.key,
                        first = null,
                        last = params.loadSize,
                        orderBy = ReleaseOrder(
                            direction = OrderDirection.DESC,
                            field_ = ReleaseOrderField.CREATED_AT
                        )
                    )
                ).data?.repository

                list.addAll(
                    repository?.releases?.nodes.orEmpty().mapNotNull { it }
                )

                val pageInfo = repository?.releases?.pageInfo?.pageInfo()

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
}