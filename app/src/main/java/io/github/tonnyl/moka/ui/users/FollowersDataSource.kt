package io.github.tonnyl.moka.ui.users

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.tonnyl.moka.data.UserItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullUserItem
import io.github.tonnyl.moka.network.queries.queryUsersFollowers
import io.github.tonnyl.moka.queries.FollowersQuery.Data.User.Followers.Nodes.Companion.userListItemFragment
import io.github.tonnyl.moka.queries.FollowersQuery.Data.User.Followers.PageInfo.Companion.pageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class FollowersDataSource(private val login: String) : PagingSource<String, UserItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, UserItem> {
        val list = mutableListOf<UserItem>()
        return withContext(Dispatchers.IO) {
            try {
                val user = queryUsersFollowers(
                    login,
                    perPage = params.loadSize,
                    after = params.key,
                    before = params.key
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