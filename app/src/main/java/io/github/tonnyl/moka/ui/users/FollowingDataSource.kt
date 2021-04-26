package io.github.tonnyl.moka.ui.users

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.data.UserItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullUserItem
import io.github.tonnyl.moka.queries.FollowingQuery
import io.github.tonnyl.moka.queries.FollowingQuery.Data.User.Following.Node.Companion.userListItemFragment
import io.github.tonnyl.moka.queries.FollowingQuery.Data.User.Following.PageInfo.Companion.pageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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
                        before = Input.Present(value = params.key),
                        after = Input.Present(value = params.key)
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
                Timber.e(e)

                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<String, UserItem>): String? {
        return null
    }

}