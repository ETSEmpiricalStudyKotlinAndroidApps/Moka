package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.FollowingQuery
import io.github.tonnyl.moka.data.UserGraphQL
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class FollowingDataSource(
    private val login: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<UserGraphQL>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource2<List<UserGraphQL>>>
) : PageKeyedDataSource<String, UserGraphQL>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, UserGraphQL>
    ) {
        Timber.d("loadInitial")

        initialLoadStatus.postValue(Resource.loading(initialLoadStatus.value?.data))

        try {
            val followingQuery = FollowingQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .build()

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(followingQuery)
                    .execute()
            }

            val list = mutableListOf<UserGraphQL>()
            val user = response.data()?.user()

            user?.following()?.nodes()?.forEach { node ->
                node?.let {
                    list.add(UserGraphQL.createFromFollowingRaw(node) ?: return@let)
                }
            }

            val pageInfo = user?.following()?.pageInfo()

            callback.onResult(
                list,
                if (pageInfo?.hasPreviousPage() == true) {
                    user.following().pageInfo().startCursor()
                } else {
                    null
                },
                if (pageInfo?.hasNextPage() == true) {
                    user.following().pageInfo().endCursor()
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

            initialLoadStatus.postValue(Resource.error(e.message, initialLoadStatus.value?.data))
        }
    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, UserGraphQL>
    ) {
        Timber.d("loadAfter")

        pagedLoadStatus.postValue(
            PagedResource2(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val followingQuery = FollowingQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(followingQuery)
                    .execute()
            }

            val list = mutableListOf<UserGraphQL>()
            val user = response.data()?.user()

            user?.following()?.nodes()?.forEach { node ->
                node?.let {
                    list.add(UserGraphQL.createFromFollowingRaw(it) ?: return@let)
                }
            }

            callback.onResult(
                list,
                if (user?.following()?.pageInfo()?.hasNextPage() == true) {
                    user.following().pageInfo().endCursor()
                } else {
                    null
                }
            )

            retry = null

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.AFTER, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadAfter(params, callback)
            }

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.AFTER, Resource.error(e.message, null))
            )
        }
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, UserGraphQL>
    ) {
        Timber.d("loadBefore")

        pagedLoadStatus.value = PagedResource2(
            PagedResourceDirection.BEFORE, Resource.loading(null)
        )

        try {
            val followersQuery = FollowingQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .before(params.key)
                .build()

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(followersQuery)
                    .execute()
            }

            val list = mutableListOf<UserGraphQL>()
            val user = response.data()?.user()

            user?.following()?.nodes()?.forEach { node ->
                node?.let {
                    list.add(UserGraphQL.createFromFollowingRaw(it) ?: return@let)
                }
            }

            callback.onResult(
                list,
                if (user?.following()?.pageInfo()?.hasPreviousPage() == true) {
                    user.following().pageInfo().startCursor()
                } else {
                    null
                }
            )

            retry = null

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.BEFORE, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadBefore(params, callback)
            }

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.BEFORE, Resource.error(e.message, null))
            )
        }
    }
}