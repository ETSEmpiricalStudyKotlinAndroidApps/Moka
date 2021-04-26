package io.github.tonnyl.moka.ui.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullRepositoryItem
import io.github.tonnyl.moka.queries.StarredRepositoriesQuery
import io.github.tonnyl.moka.queries.StarredRepositoriesQuery.Data.User.StarredRepositories.Node.Companion.repositoryListItemFragment
import io.github.tonnyl.moka.queries.StarredRepositoriesQuery.Data.User.StarredRepositories.PageInfo.Companion.pageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class StarredRepositoriesDataSource(
    private val apolloClient: ApolloClient,
    private val login: String
) : PagingSource<String, RepositoryItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RepositoryItem> {
        val list = mutableListOf<RepositoryItem>()
        return withContext(Dispatchers.IO) {
            try {
                val user = apolloClient.query(
                    query = StarredRepositoriesQuery(
                        login = login,
                        perPage = params.loadSize,
                        after = Input.Present(value = params.key),
                        Input.Present(value = params.key)
                    )
                ).data?.user

                list.addAll(
                    user?.starredRepositories?.nodes.orEmpty().mapNotNull { node ->
                        node?.repositoryListItemFragment()?.toNonNullRepositoryItem()
                    }
                )

                val pageInfo = user?.starredRepositories?.pageInfo?.pageInfo()

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

    override fun getRefreshKey(state: PagingState<String, RepositoryItem>): String? {
        return null
    }

}