package io.github.tonnyl.moka.ui.users

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.UserItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullUserItem
import io.github.tonnyl.moka.queries.FollowersQuery
import io.github.tonnyl.moka.queries.FollowersQuery.Data.User.Followers.Node.Companion.userListItemFragment
import io.github.tonnyl.moka.queries.FollowersQuery.Data.User.Followers.PageInfo.Companion.pageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class FollowersDataSource(
    private val apolloClient: ApolloClient,
    private val login: String
) : PagingSource<String, UserItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, UserItem> {
        val list = mutableListOf<UserItem>()
        return withContext(Dispatchers.IO) {
            try {
                val user = apolloClient.query(
                    query = FollowersQuery(
                        login = login,
                        perPage = params.loadSize,
                        before = params.key,
                        after = params.key
                    )
                ).data?.user

                list.addAll(
                    user?.followers?.nodes.orEmpty().mapNotNull { node ->
                        node?.userListItemFragment()?.toNonNullUserItem()
                    }
                )

                val pageInfo = user?.followers?.pageInfo?.pageInfo()

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

    override fun getRefreshKey(state: PagingState<String, UserItem>): String? {
        return null
    }

}