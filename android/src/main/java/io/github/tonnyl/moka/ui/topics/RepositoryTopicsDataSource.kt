package io.github.tonnyl.moka.ui.topics

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import io.tonnyl.moka.common.data.extension.checkedEndCursor
import io.tonnyl.moka.common.data.extension.checkedStartCursor
import io.tonnyl.moka.graphql.RepositoryTopicsQuery
import io.tonnyl.moka.graphql.fragment.RepositoryTopic
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
                        after = Optional.presentIfNotNull(params.key),
                        before = Optional.presentIfNotNull(params.key),
                        first = Optional.presentIfNotNull(params.loadSize),
                        last = Optional.Absent
                    )
                ).execute().data?.repository?.repositoryTopics

                list.addAll(
                    response?.nodes.orEmpty().mapNotNull { node ->
                        node?.repositoryTopic
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