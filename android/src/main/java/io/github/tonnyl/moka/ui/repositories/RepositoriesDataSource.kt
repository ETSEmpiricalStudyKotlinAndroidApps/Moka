package io.github.tonnyl.moka.ui.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import io.tonnyl.moka.common.data.extension.checkedEndCursor
import io.tonnyl.moka.common.data.extension.checkedStartCursor
import io.tonnyl.moka.graphql.OwnedRepositoriesQuery
import io.tonnyl.moka.graphql.RepositoryForksQuery
import io.tonnyl.moka.graphql.StarredRepositoriesQuery
import io.tonnyl.moka.graphql.fragment.RepositoryListItemFragment
import io.tonnyl.moka.graphql.type.RepositoryAffiliation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class RepositoriesDataSource(
    private val apolloClient: ApolloClient,
    private val login: String,
    private val repoName: String?,
    private val queryOption: RepositoriesQueryOption
) : PagingSource<String, RepositoryListItemFragment>() {

    override fun getRefreshKey(state: PagingState<String, RepositoryListItemFragment>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RepositoryListItemFragment> {
        val list = mutableListOf<RepositoryListItemFragment>()
        return withContext(Dispatchers.IO) {
            try {
                val pageInfo = when (queryOption) {
                    is RepositoriesQueryOption.Forks -> {
                        val repo = apolloClient.query(
                            query = RepositoryForksQuery(
                                login = login,
                                repo = repoName!!,
                                perPage = params.loadSize,
                                after = Optional.presentIfNotNull(params.key),
                                before = Optional.presentIfNotNull(params.key),
                                orderBy = Optional.presentIfNotNull(value = queryOption.order)
                            )
                        ).execute().data?.repository

                        list.addAll(
                            repo?.forks?.nodes.orEmpty().mapNotNull { node ->
                                node?.repositoryListItemFragment
                            }
                        )

                        repo?.forks?.pageInfo?.pageInfo
                    }
                    is RepositoriesQueryOption.Owned -> {
                        val affiliations = mutableListOf<RepositoryAffiliation>()
                        if (queryOption.isAffiliationCollaborator) {
                            affiliations.add(RepositoryAffiliation.COLLABORATOR)
                        }
                        if (queryOption.isAffiliationOwner) {
                            affiliations.add(RepositoryAffiliation.OWNER)
                        }

                        val user = apolloClient.query(
                            query = OwnedRepositoriesQuery(
                                login = login,
                                perPage = params.loadSize,
                                after = Optional.presentIfNotNull(params.key),
                                before = Optional.presentIfNotNull(params.key),
                                affiliations = Optional.presentIfNotNull(value = affiliations),
                                orderBy = Optional.presentIfNotNull(value = queryOption.order),
                                privacy = Optional.presentIfNotNull(value = queryOption.privacy)
                            )
                        ).execute().data?.user

                        list.addAll(
                            user?.repositories?.nodes.orEmpty().mapNotNull { node ->
                                node?.repositoryListItemFragment
                            }
                        )

                        user?.repositories?.pageInfo?.pageInfo
                    }
                    is RepositoriesQueryOption.Starred -> {
                        val user = apolloClient.query(
                            query = StarredRepositoriesQuery(
                                login = login,
                                perPage = params.loadSize,
                                after = Optional.presentIfNotNull(params.key),
                                before = Optional.presentIfNotNull(params.key),
                                orderBy = Optional.presentIfNotNull(value = queryOption.order)
                            )
                        ).execute().data?.user

                        list.addAll(
                            user?.starredRepositories?.nodes.orEmpty().mapNotNull { node ->
                                node?.repositoryListItemFragment
                            }
                        )

                        user?.starredRepositories?.pageInfo?.pageInfo
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