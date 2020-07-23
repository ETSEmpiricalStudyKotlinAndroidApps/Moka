package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import io.github.tonnyl.moka.data.UserItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullUserItem
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryUsersFollowers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class FollowersDataSource(
    private val login: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<UserItem>>>
) : PagingSource<String, UserItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, UserItem> {
        val list = mutableListOf<UserItem>()
        return withContext(Dispatchers.IO) {
            try {
                if (params is LoadParams.Refresh) {
                    initialLoadStatus.postValue(Resource.loading(null))
                }

                val user = queryUsersFollowers(
                    login,
                    perPage = params.loadSize,
                    after = params.key,
                    before = params.key
                ).data()?.user

                user?.followers?.nodes?.forEach { node ->
                    node?.let {
                        list.add(it.fragments.userListItemFragment.toNonNullUserItem())
                    }
                }

                val pageInfo = user?.followers?.pageInfo?.fragments?.pageInfo

                LoadResult.Page(
                    data = list,
                    prevKey = pageInfo.checkedStartCursor,
                    nextKey = pageInfo.checkedEndCursor
                ).also {
                    if (params is LoadParams.Refresh) {
                        initialLoadStatus.postValue(Resource.success(list))
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)

                if (params is LoadParams.Refresh) {
                    initialLoadStatus.postValue(Resource.error(e.message, null))
                }

                LoadResult.Error<String, UserItem>(e)
            }
        }
    }

}