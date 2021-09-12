package io.github.tonnyl.moka.ui.commits

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.fragment.CommitListItem
import io.github.tonnyl.moka.queries.RepositoryCommitsQuery
import io.github.tonnyl.moka.queries.RepositoryCommitsQuery.Data.Repository.Ref.CommitTarget.History.Node.Companion.commitListItem
import io.github.tonnyl.moka.queries.RepositoryCommitsQuery.Data.Repository.Ref.CommitTarget.History.PageInfo.Companion.pageInfo
import io.github.tonnyl.moka.queries.RepositoryCommitsQuery.Data.Repository.Ref.Target.Companion.asCommit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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
                        after = params.key,
                        before = params.key,
                        first = params.loadSize,
                        last = null
                    )
                ).data?.repository?.ref?.target?.asCommit()?.history

                list.addAll(response?.nodes.orEmpty().mapNotNull { it?.commitListItem() })

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