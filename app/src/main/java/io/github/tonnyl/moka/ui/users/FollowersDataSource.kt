package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.MutableLiveData
import io.github.tonnyl.moka.data.UserItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullUserItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryUsersFollowers
import io.github.tonnyl.moka.ui.*

class FollowersDataSource(
    private val login: String,
    override val initial: MutableLiveData<Resource<List<UserItem>>>,
    override val previousOrNext: MutableLiveData<PagedResource<List<UserItem>>>
) : PageKeyedDataSourceWithLoadState<UserItem>() {

    override fun doLoadInitial(params: LoadInitialParams<String>): InitialLoadResponse<UserItem> {
        val response = queryUsersFollowers(
            login = login,
            perPage = params.requestedLoadSize
        )

        val list = mutableListOf<UserItem>()
        val user = response.data()?.user

        user?.followers?.nodes?.forEach { node ->
            node?.let {
                list.add(node.fragments.userListItemFragment.toNonNullUserItem())
            }
        }

        val pageInfo = user?.followers?.pageInfo?.fragments?.pageInfo

        return InitialLoadResponse(
            list,
            PreviousPageKey(pageInfo.checkedStartCursor),
            NextPageKey(pageInfo.checkedEndCursor)
        )
    }

    override fun doLoadAfter(params: LoadParams<String>): AfterLoadResponse<UserItem> {
        val response = queryUsersFollowers(
            login = login,
            perPage = params.requestedLoadSize,
            after = params.key
        )

        val list = mutableListOf<UserItem>()
        val user = response.data()?.user

        user?.followers?.nodes?.forEach { node ->
            node?.let {
                list.add(node.fragments.userListItemFragment.toNonNullUserItem())
            }
        }

        return AfterLoadResponse(
            list,
            NextPageKey(user?.followers?.pageInfo?.fragments?.pageInfo.checkedEndCursor)
        )
    }

    override fun doLoadBefore(params: LoadParams<String>): BeforeLoadResponse<UserItem> {
        val response = queryUsersFollowers(
            login = login,
            perPage = params.requestedLoadSize,
            before = params.key
        )

        val list = mutableListOf<UserItem>()
        val user = response.data()?.user

        user?.followers?.nodes?.forEach { node ->
            node?.let {
                list.add(node.fragments.userListItemFragment.toNonNullUserItem())
            }
        }

        return BeforeLoadResponse(
            list,
            PreviousPageKey(user?.followers?.pageInfo?.fragments?.pageInfo.checkedStartCursor)
        )
    }

}