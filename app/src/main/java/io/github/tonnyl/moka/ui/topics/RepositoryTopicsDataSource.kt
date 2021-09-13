package io.github.tonnyl.moka.ui.topics

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.RepositoryTopic
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullRepositoryTopic
import io.github.tonnyl.moka.queries.RepositoryTopicsQuery
import io.github.tonnyl.moka.queries.RepositoryTopicsQuery.Data.Repository.RepositoryTopics.PageInfo.Companion.pageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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
                ).data?.repository?.repositoryTopics

                list.addAll(
                    response?.nodes.orEmpty().mapNotNull { node ->
                        node?.toNonNullRepositoryTopic()
                    }
                )

                val pageInfo = response?.pageInfo?.pageInfo()

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

}