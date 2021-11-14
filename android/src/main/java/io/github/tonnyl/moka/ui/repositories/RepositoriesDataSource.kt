package io.github.tonnyl.moka.ui.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullRepositoryItem
import io.tonnyl.moka.graphql.OwnedRepositoriesQuery
import io.tonnyl.moka.graphql.RepositoryForksQuery
import io.tonnyl.moka.graphql.StarredRepositoriesQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class RepositoriesDataSource(
    private val apolloClient: ApolloClient,
    private val login: String,
    private val repoName: String?,
    private val repositoryType: RepositoryType
) : PagingSource<String, RepositoryItem>() {

    override fun getRefreshKey(state: PagingState<String, RepositoryItem>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RepositoryItem> {
        val list = mutableListOf<RepositoryItem>()
        return withContext(Dispatchers.IO) {
            try {
                val pageInfo = when (repositoryType) {
                    RepositoryType.STARRED -> {
                        val user = apolloClient.query(
                            query = StarredRepositoriesQuery(
                                login = login,
                                perPage = params.loadSize,
                                after = params.key,
                                before = params.key
                            )
                        ).execute().data?.user

                        list.addAll(
                            user?.starredRepositories?.nodes.orEmpty().mapNotNull { node ->
                                node?.repositoryListItemFragment?.toNonNullRepositoryItem()
                            }
                        )

                        user?.starredRepositories?.pageInfo?.pageInfo
                    }
                    RepositoryType.OWNED -> {
                        val user = apolloClient.query(
                            query = OwnedRepositoriesQuery(
                                login = login,
                                perPage = params.loadSize,
                                after = params.key,
                                before = params.key
                            )
                        ).execute().data?.user

                        list.addAll(
                            user?.repositories?.nodes.orEmpty().mapNotNull { node ->
                                node?.repositoryListItemFragment?.toNonNullRepositoryItem()
                            }
                        )

                        user?.repositories?.pageInfo?.pageInfo
                    }
                    RepositoryType.FORKS -> {
                        val repo = apolloClient.query(
                            query = RepositoryForksQuery(
                                login = login,
                                repo = repoName!!,
                                perPage = params.loadSize,
                                after = params.key,
                                before = params.key
                            )
                        ).execute().data?.repository

                        list.addAll(
                            repo?.forks?.nodes.orEmpty().mapNotNull { node ->
                                node?.repositoryListItemFragment?.toNonNullRepositoryItem()
                            }
                        )

                        repo?.forks?.pageInfo?.pageInfo
                    }
                }

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