package io.github.tonnyl.moka.ui.branches

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import io.tonnyl.moka.common.data.extension.checkedEndCursor
import io.tonnyl.moka.common.data.extension.checkedStartCursor
import io.tonnyl.moka.graphql.RepositoryRefsQuery
import io.tonnyl.moka.graphql.fragment.Ref
import io.tonnyl.moka.graphql.type.OrderDirection
import io.tonnyl.moka.graphql.type.RefOrder
import io.tonnyl.moka.graphql.type.RefOrderField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class BranchesDataSource(
    private val apolloClient: ApolloClient,
    private val login: String,
    private val repoName: String,
    private val refPrefix: String
) : PagingSource<String, Ref>() {

    var query: String? = null
    var sort: RefOrder = RefOrder(
        direction = OrderDirection.ASC,
        field = RefOrderField.ALPHABETICAL
    )

    override fun getRefreshKey(state: PagingState<String, Ref>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Ref> {
        val list = mutableListOf<Ref>()

        return withContext(Dispatchers.IO) {
            try {
                val response = apolloClient.query(
                    query = RepositoryRefsQuery(
                        login = login,
                        repoName = repoName,
                        after = Optional.presentIfNotNull(params.key),
                        before = Optional.presentIfNotNull(params.key),
                        perPage = params.loadSize,
                        query = Optional.presentIfNotNull(query),
                        refPrefix = refPrefix,
                        orderBy = Optional.presentIfNotNull(sort)
                    )
                ).execute().data?.repository?.refs

                list.addAll(response?.nodes?.mapNotNull { it?.ref }.orEmpty())

                val pageInfo = response?.pageInfo?.pageInfo

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