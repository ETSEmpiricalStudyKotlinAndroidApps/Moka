package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.data.UserItem
import io.github.tonnyl.moka.data.toNonNullUserItem
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.queries.FollowersQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class FollowersDataSource(
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
            val followerQuery = FollowersQuery(
                login,
                params.requestedLoadSize,
                Input.absent(),
                Input.absent()
            )

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(followerQuery)
                    .execute()
            }

            val list = mutableListOf<UserItem>()
            val user = response.data()?.user

            user?.followers?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.fragments.userListItemFragment.toNonNullUserItem())
                }
            }

            val pageInfo = user?.followers?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                if (pageInfo?.hasNextPage == true) {
                    pageInfo.startCursor
                } else {
                    null
                },
                if (pageInfo?.hasNextPage == true) {
                    pageInfo.endCursor
                } else {
                    null
                }
            )

            retry = null

            initialLoadStatus.postValue(Resource.success(list))
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadInitial(params, callback)
            }

            initialLoadStatus.postValue(Resource.error(e.message, null))
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
            val followersQuery = FollowersQuery(
                login,
                params.requestedLoadSize,
                Input.absent(),
                Input.fromNullable(params.key)
            )

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(followersQuery)
                    .execute()
            }

            val list = mutableListOf<UserItem>()
            val user = response.data()?.user

            user?.followers?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.fragments.userListItemFragment.toNonNullUserItem())
                }
            }

            val pageInfo = user?.followers?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                if (pageInfo?.hasNextPage == true) {
                    pageInfo.endCursor
                } else {
                    null
                }
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

        pagedLoadStatus.postValue(
            PagedResource(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val followersQuery = FollowersQuery(
                login,
                params.requestedLoadSize,
                Input.optional(params.key),
                Input.absent()
            )

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(followersQuery)
                    .execute()
            }

            val list = mutableListOf<UserItem>()
            val user = response.data()?.user

            user?.followers?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.fragments.userListItemFragment.toNonNullUserItem())
                }
            }

            val pageInfo = user?.followers?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                if (pageInfo?.hasPreviousPage == true) {
                    pageInfo.startCursor
                } else {
                    null
                }
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