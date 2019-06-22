package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.coroutines.toDeferred
import io.github.tonnyl.moka.FollowingQuery
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.data.UserGraphQL
import io.github.tonnyl.moka.network.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class FollowingDataSource(
    private val coroutineScope: CoroutineScope,
    private val login: String,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<UserGraphQL>>>
) : PageKeyedDataSource<String, UserGraphQL>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, UserGraphQL>) {
        Timber.d("loadInitial")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(initial = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val followingQuery = FollowingQuery.builder()
                        .login(login)
                        .perPage(params.requestedLoadSize)
                        .build()

                    NetworkClient.apolloClient.query(followingQuery).toDeferred()
                }.await()

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
                    if (pageInfo?.hasPreviousPage() == true) user.following().pageInfo().startCursor() else null,
                    if (pageInfo?.hasNextPage() == true) user.following().pageInfo().endCursor() else null
                )

                retry = null

                loadStatusLiveData.value = PagedResource(initial = Resource.success(list))
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadInitial(params, callback)
                }

                loadStatusLiveData.value = PagedResource(initial = Resource.error(e.message, null))
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, UserGraphQL>) {
        Timber.d("loadAfter")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(after = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val followingQuery = FollowingQuery.builder()
                        .login(login)
                        .perPage(params.requestedLoadSize)
                        .after(params.key)
                        .build()

                    NetworkClient.apolloClient.query(followingQuery).toDeferred()
                }.await()

                val list = mutableListOf<UserGraphQL>()
                val user = response.data()?.user()

                user?.following()?.nodes()?.forEach { node ->
                    node?.let {
                        list.add(UserGraphQL.createFromFollowingRaw(it) ?: return@let)
                    }
                }

                callback.onResult(
                    list,
                    if (user?.following()?.pageInfo()?.hasNextPage() == true) user.following().pageInfo().endCursor() else null
                )

                retry = null

                loadStatusLiveData.value = PagedResource(after = Resource.success(list))
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.value = PagedResource(after = Resource.error(e.message, null))
            }
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, UserGraphQL>) {
        Timber.d("loadBefore")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(before = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val followersQuery = FollowingQuery.builder()
                        .login(login)
                        .perPage(params.requestedLoadSize)
                        .before(params.key)
                        .build()

                    NetworkClient.apolloClient.query(followersQuery).toDeferred()
                }.await()

                val list = mutableListOf<UserGraphQL>()
                val user = response.data()?.user()

                user?.following()?.nodes()?.forEach { node ->
                    node?.let {
                        list.add(UserGraphQL.createFromFollowingRaw(it) ?: return@let)
                    }
                }

                callback.onResult(
                    list,
                    if (user?.following()?.pageInfo()?.hasPreviousPage() == true) user.following().pageInfo().startCursor() else null
                )

                retry = null

                loadStatusLiveData.value = PagedResource(before = Resource.success(list))
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadBefore(params, callback)
                }

                loadStatusLiveData.value = PagedResource(before = Resource.error(e.message, null))
            }
        }
    }
}