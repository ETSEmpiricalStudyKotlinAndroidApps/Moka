package io.github.tonnyl.moka.ui.users

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.UserItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullUserItem
import io.github.tonnyl.moka.queries.FollowingQuery
import io.github.tonnyl.moka.queries.FollowingQuery.Data.User.Following.Node.Companion.userListItemFragment
import io.github.tonnyl.moka.queries.FollowingQuery.Data.User.Following.PageInfo.Companion.pageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class FollowingDataSource(
    private val apolloClient: ApolloClient,
    private val login: String
) : PagingSource<String, UserItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, UserItem> {
        val list = mutableListOf<UserItem>()
        return withContext(Dispatchers.IO) {
            try {
                val user = apolloClient.query(
                    query = FollowingQuery(
                        login = login,
                        perPage = params.loadSize,
                        before = params.key,
                        after = params.key
                    )
                ).data?.user

                list.addAll(
                    user?.following?.nodes.orEmpty().mapNotNull { node ->
                        node?.userListItemFragment()?.toNonNullUserItem()
                    }
                )

                val pageInfo = user?.following?.pageInfo?.pageInfo()

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

    override fun getRefreshKey(state: PagingState<String, UserItem>): String? {
        return null
    }

}