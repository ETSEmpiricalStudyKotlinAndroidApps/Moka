package io.github.tonnyl.moka.ui.users

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import io.tonnyl.moka.common.data.extension.checkedEndCursor
import io.tonnyl.moka.common.data.extension.checkedStartCursor
import io.tonnyl.moka.graphql.FollowersQuery
import io.tonnyl.moka.graphql.FollowingQuery
import io.tonnyl.moka.graphql.RepositoryStargazersQuery
import io.tonnyl.moka.graphql.RepositoryWatchersQuery
import io.tonnyl.moka.graphql.fragment.UserListItemFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class UsersDataSource(
    private val apolloClient: ApolloClient,
    private val login: String,
    private val repoName: String?,
    private val usersType: UsersType
) : PagingSource<String, UserListItemFragment>() {

    override fun getRefreshKey(state: PagingState<String, UserListItemFragment>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, UserListItemFragment> {
        val list = mutableListOf<UserListItemFragment>()
        return withContext(Dispatchers.IO) {
            try {
                val pageInfo = when (usersType) {
                    UsersType.FOLLOWER -> {
                        val user = apolloClient.query(
                            query = FollowersQuery(
                                login = login,
                                perPage = params.loadSize,
                                before = Optional.presentIfNotNull(params.key),
                                after = Optional.presentIfNotNull(params.key)
                            )
                        ).execute().data?.user

                        list.addAll(
                            user?.followers?.nodes.orEmpty().mapNotNull { node ->
                                node?.userListItemFragment
                            }
                        )

                        user?.followers?.pageInfo?.pageInfo
                    }
                    UsersType.FOLLOWING -> {
                        val user = apolloClient.query(
                            query = FollowingQuery(
                                login = login,
                                perPage = params.loadSize,
                                before = Optional.presentIfNotNull(params.key),
                                after = Optional.presentIfNotNull(params.key)
                            )
                        ).execute().data?.user

                        list.addAll(
                            user?.following?.nodes.orEmpty().mapNotNull { node ->
                                node?.userListItemFragment
                            }
                        )

                        user?.following?.pageInfo?.pageInfo
                    }
                    UsersType.REPOSITORY_STARGAZERS -> {
                        val repo = apolloClient.query(
                            query = RepositoryStargazersQuery(
                                login = login,
                                repo = repoName!!,
                                perPage = params.loadSize,
                                before = Optional.presentIfNotNull(params.key),
                                after = Optional.presentIfNotNull(params.key)
                            )
                        ).execute().data?.repository

                        list.addAll(
                            repo?.stargazers?.nodes.orEmpty().mapNotNull { node ->
                                node?.userListItemFragment
                            }
                        )

                        repo?.stargazers?.pageInfo?.pageInfo
                    }
                    UsersType.REPOSITORY_WATCHERS -> {
                        val repo = apolloClient.query(
                            query = RepositoryWatchersQuery(
                                login = login,
                                repo = repoName!!,
                                perPage = params.loadSize,
                                before = Optional.presentIfNotNull(params.key),
                                after = Optional.presentIfNotNull(params.key)
                            )
                        ).execute().data?.repository

                        list.addAll(
                            repo?.watchers?.nodes.orEmpty().mapNotNull { node ->
                                node?.userListItemFragment
                            }
                        )

                        repo?.watchers?.pageInfo?.pageInfo
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