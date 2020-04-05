package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.UserItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullUserItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryUsersFollowing
import timber.log.Timber

class FollowingDataSource(
    private val login: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<UserItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource<List<UserItem>>>
) : PageKeyedDataSource<String, UserItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, UserItem>
    ) {
        Timber.d("loadInitial")

        initialLoadStatus.postValue(Resource.loading(initialLoadStatus.value?.data))

        try {
            val response = queryUsersFollowing(
                login,
                perPage = params.requestedLoadSize
            )

            val list = mutableListOf<UserItem>()
            val user = response.data()?.user

            user?.following?.nodes?.forEach { node ->
                node?.let {
                    list.add(it.fragments.userListItemFragment.toNonNullUserItem())
                }
            }

            val pageInfo = user?.following?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                pageInfo.checkedStartCursor,
                pageInfo.checkedEndCursor
            )

            retry = null

            initialLoadStatus.postValue(Resource.success(list))
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadInitial(params, callback)
            }

            initialLoadStatus.postValue(Resource.error(e.message, initialLoadStatus.value?.data))
        }
    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, UserItem>
    ) {
        Timber.d("loadAfter")

        pagedLoadStatus.postValue(
            PagedResource(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val response = queryUsersFollowing(
                login = login,
                perPage = params.requestedLoadSize,
                after = params.key
            )

            val list = mutableListOf<UserItem>()
            val user = response.data()?.user

            user?.following?.nodes?.forEach { node ->
                node?.let {
                    list.add(it.fragments.userListItemFragment.toNonNullUserItem())
                }
            }

            callback.onResult(
                list,
                user?.following?.pageInfo?.fragments?.pageInfo.checkedEndCursor
            )

            retry = null

            pagedLoadStatus.postValue(
                PagedResource(PagedResourceDirection.AFTER, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadAfter(params, callback)
            }

            pagedLoadStatus.postValue(
                PagedResource(PagedResourceDirection.AFTER, Resource.error(e.message, null))
            )
        }
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, UserItem>
    ) {
        Timber.d("loadBefore")

        pagedLoadStatus.value = PagedResource(
            PagedResourceDirection.BEFORE, Resource.loading(null)
        )

        try {
            val response = queryUsersFollowing(
                login = login,
                perPage = params.requestedLoadSize,
                before = params.key
            )

            val list = mutableListOf<UserItem>()
            val user = response.data()?.user

            user?.following?.nodes?.forEach { node ->
                node?.let {
                    list.add(it.fragments.userListItemFragment.toNonNullUserItem())
                }
            }

            callback.onResult(
                list,
                user?.following?.pageInfo?.fragments?.pageInfo.checkedStartCursor
            )

            retry = null

            pagedLoadStatus.postValue(
                PagedResource(PagedResourceDirection.BEFORE, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadBefore(params, callback)
            }

            pagedLoadStatus.postValue(
                PagedResource(PagedResourceDirection.BEFORE, Resource.error(e.message, null))
            )
        }
    }
}