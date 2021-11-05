package io.github.tonnyl.moka.ui.topics

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.RepositoryTopic
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullRepositoryTopic
import io.tonnyl.moka.graphql.RepositoryTopicsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class RepositoryTopicsDataSource(
    private val apolloClient: ApolloClient,
    private val login: String,
    private val name: String
) : PagingSource<String, RepositoryTopic>() {

    override fun getRefreshKey(state: PagingState<String, RepositoryTopic>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RepositoryTopic> {
        val list = mutableListOf<RepositoryTopic>()

        return withContext(Dispatchers.IO) {
            try {
                val response = apolloClient.query(
                    query = RepositoryTopicsQuery(
                        login = login,
                        repoName = name,
                        after = params.key,
                        before = params.key,
                        first = params.loadSize,
                        last = null
                    )
                ).execute().data?.repository?.repositoryTopics

                list.addAll(
                    response?.nodes.orEmpty().mapNotNull { node ->
                        node?.repositoryTopic?.toNonNullRepositoryTopic()
                    }
                )

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