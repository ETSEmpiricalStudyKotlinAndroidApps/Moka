package io.github.tonnyl.moka.ui.commits

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import io.tonnyl.moka.common.data.extension.checkedEndCursor
import io.tonnyl.moka.common.data.extension.checkedStartCursor
import io.tonnyl.moka.graphql.RepositoryCommitsQuery
import io.tonnyl.moka.graphql.fragment.CommitListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class CommitsDataSource(
    private val apolloClient: ApolloClient,
    private val login: String,
    private val repoName: String,
    private val qualifiedName: String
) : PagingSource<String, CommitListItem>() {

    override fun getRefreshKey(state: PagingState<String, CommitListItem>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, CommitListItem> {
        val list = mutableListOf<CommitListItem>()

        return withContext(Dispatchers.IO) {
            try {
                val response = apolloClient.query(
                    query = RepositoryCommitsQuery(
                        login = login,
                        repoName = repoName,
                        qualifiedName = qualifiedName,
                        after = Optional.presentIfNotNull(params.key),
                        before = Optional.presentIfNotNull(params.key),
                        first = Optional.presentIfNotNull(params.loadSize),
                        last = Optional.Absent
                    )
                ).execute().data?.repository?.ref?.target?.onCommit?.history

                list.addAll(response?.nodes.orEmpty().mapNotNull { it?.commitListItem })

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