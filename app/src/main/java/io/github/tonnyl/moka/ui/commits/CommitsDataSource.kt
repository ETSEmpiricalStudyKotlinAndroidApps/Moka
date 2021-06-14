package io.github.tonnyl.moka.ui.commits

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.fragment.CommitListItem
import io.github.tonnyl.moka.queries.OrganizationsRepositoryDefaultCommitsQuery
import io.github.tonnyl.moka.queries.OrganizationsRepositoryDefaultCommitsQuery.Data.Organization.Repository.DefaultBranchRef.CommitTarget.History.Node.Companion.commitListItem
import io.github.tonnyl.moka.queries.OrganizationsRepositoryDefaultCommitsQuery.Data.Organization.Repository.DefaultBranchRef.CommitTarget.History.PageInfo.Companion.pageInfo
import io.github.tonnyl.moka.queries.OrganizationsRepositoryDefaultCommitsQuery.Data.Organization.Repository.DefaultBranchRef.Target.Companion.asCommit
import io.github.tonnyl.moka.queries.UsersRepositoryDefaultCommitsQuery
import io.github.tonnyl.moka.queries.UsersRepositoryDefaultCommitsQuery.Data.User.Repository.DefaultBranchRef.CommitTarget.History.Node.Companion.commitListItem
import io.github.tonnyl.moka.queries.UsersRepositoryDefaultCommitsQuery.Data.User.Repository.DefaultBranchRef.CommitTarget.History.PageInfo.Companion.pageInfo
import io.github.tonnyl.moka.queries.UsersRepositoryDefaultCommitsQuery.Data.User.Repository.DefaultBranchRef.Target.Companion.asCommit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class CommitsDataSource(
    private val apolloClient: ApolloClient,
    private val isOrg: Boolean,
    private val login: String,
    private val repoName: String
) : PagingSource<String, CommitListItem>() {

    override fun getRefreshKey(state: PagingState<String, CommitListItem>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, CommitListItem> {
        val list = mutableListOf<CommitListItem>()

        return withContext(Dispatchers.IO) {
            try {
                val (prevKey, nextKey) = if (isOrg) {
                    val response = apolloClient.query(
                        query = OrganizationsRepositoryDefaultCommitsQuery(
                            login = login,
                            repoName = repoName,
                            after = params.key,
                            before = params.key,
                            first = params.loadSize,
                            last = null
                        )
                    ).data?.organization?.repository?.defaultBranchRef?.target?.asCommit()?.history

                    list.addAll(response?.nodes.orEmpty().mapNotNull { it?.commitListItem() })

                    val pageInfo = response?.pageInfo?.pageInfo()

                    Pair(
                        first = pageInfo.checkedStartCursor,
                        second = pageInfo.checkedEndCursor
                    )
                } else {
                    val response = apolloClient.query(
                        query = UsersRepositoryDefaultCommitsQuery(
                            login = login,
                            repoName = repoName,
                            after = params.key,
                            before = params.key,
                            first = params.loadSize,
                            last = null
                        )
                    ).data?.user?.repository?.defaultBranchRef?.target?.asCommit()?.history

                    list.addAll(response?.nodes.orEmpty().mapNotNull { it?.commitListItem() })

                    val pageInfo = response?.pageInfo?.pageInfo()

                    Pair(
                        first = pageInfo.checkedStartCursor,
                        second = pageInfo.checkedEndCursor
                    )
                }
                LoadResult.Page(
                    data = list,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } catch (e: Exception) {
                Timber.e(e)

                LoadResult.Error(e)
            }
        }
    }

}