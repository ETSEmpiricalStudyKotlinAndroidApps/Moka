package io.github.tonnyl.moka.ui.users

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.UserItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullUserItem
import io.tonnyl.moka.graphql.FollowersQuery
import io.tonnyl.moka.graphql.FollowingQuery
import io.tonnyl.moka.graphql.RepositoryStargazersQuery
import io.tonnyl.moka.graphql.RepositoryWatchersQuery
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
) : PagingSource<String, UserItem>() {

    override fun getRefreshKey(state: PagingState<String, UserItem>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, UserItem> {
        val list = mutableListOf<UserItem>()
        return withContext(Dispatchers.IO) {
            try {
                val pageInfo = when (usersType) {
                    UsersType.FOLLOWER -> {
                        val user = apolloClient.query(
                            query = FollowersQuery(
                                login = login,
                                perPage = params.loadSize,
                                before = params.key,
                                after = params.key
                            )
                        ).execute().data?.user

                        list.addAll(
                            user?.followers?.nodes.orEmpty().mapNotNull { node ->
                                node?.userListItemFragment?.toNonNullUserItem()
                            }
                        )

                        user?.followers?.pageInfo?.pageInfo
                    }
                    UsersType.FOLLOWING -> {
                        val user = apolloClient.query(
                            query = FollowingQuery(
                                login = login,
                                perPage = params.loadSize,
                                before = params.key,
                                after = params.key
                            )
                        ).execute().data?.user

                        list.addAll(
                            user?.following?.nodes.orEmpty().mapNotNull { node ->
                                node?.userListItemFragment?.toNonNullUserItem()
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
                                before = params.key,
                                after = params.key
                            )
                        ).execute().data?.repository

                        list.addAll(
                            repo?.stargazers?.nodes.orEmpty().mapNotNull { node ->
                                node?.userListItemFragment?.toNonNullUserItem()
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
                                before = params.key,
                                after = params.key
                            )
                        ).execute().data?.repository

                        list.addAll(
                            repo?.watchers?.nodes.orEmpty().mapNotNull { node ->
                                node?.userListItemFragment?.toNonNullUserItem()
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