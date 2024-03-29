package io.github.tonnyl.moka.ui.releases

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import io.tonnyl.moka.common.data.extension.checkedEndCursor
import io.tonnyl.moka.common.data.extension.checkedStartCursor
import io.tonnyl.moka.graphql.RepositoryReleasesQuery
import io.tonnyl.moka.graphql.fragment.ReleaseListItem
import io.tonnyl.moka.graphql.type.OrderDirection
import io.tonnyl.moka.graphql.type.ReleaseOrder
import io.tonnyl.moka.graphql.type.ReleaseOrderField
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
                        after = Optional.presentIfNotNull(params.key),
                        before = Optional.presentIfNotNull(params.key),
                        first = Optional.presentIfNotNull(params.loadSize),
                        last = Optional.Absent,
                        orderBy = Optional.presentIfNotNull(
                            ReleaseOrder(
                                direction = OrderDirection.DESC,
                                field = ReleaseOrderField.CREATED_AT
                            )
                        )
                    )
                ).execute().data?.repository

                list.addAll(
                    repository?.releases?.nodes.orEmpty().mapNotNull { it?.releaseListItem }
                )

                val pageInfo = repository?.releases?.pageInfo?.pageInfo

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