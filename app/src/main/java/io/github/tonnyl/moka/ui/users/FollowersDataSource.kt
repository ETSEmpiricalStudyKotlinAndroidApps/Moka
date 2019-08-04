package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.coroutines.toDeferred
import io.github.tonnyl.moka.FollowersQuery
import io.github.tonnyl.moka.data.UserGraphQL
import io.github.tonnyl.moka.network.NetworkClient
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class FollowersDataSource(
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
                    val followerQuery = FollowersQuery.builder()
                        .login(login)
                        .perPage(params.requestedLoadSize)
                        .build()

                    NetworkClient.apolloClient.query(followerQuery).toDeferred()
                }.await()

                val list = mutableListOf<UserGraphQL>()
                val user = response.data()?.user()

                user?.followers()?.nodes()?.forEach { node ->
                    node?.let {
                        list.add(UserGraphQL.createFromFollowerRaw(node) ?: return@let)
                    }
                }

                val pageInfo = user?.followers()?.pageInfo()

                callback.onResult(
                    list,
                    if (pageInfo?.hasNextPage() == true) user.followers().pageInfo().startCursor() else null,
                    if (pageInfo?.hasNextPage() == true) user.followers().pageInfo().endCursor() else null
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
                    val followersQuery = FollowersQuery.builder()
                        .login(login)
                        .perPage(params.requestedLoadSize)
                        .after(params.key)
                        .build()

                    NetworkClient.apolloClient.query(followersQuery).toDeferred()
                }.await()

                val list = mutableListOf<UserGraphQL>()
                val user = response.data()?.user()

                user?.followers()?.nodes()?.forEach { node ->
                    node?.let {
                        list.add(UserGraphQL.createFromFollowerRaw(it) ?: return@let)
                    }
                }

                callback.onResult(
                    list,
                    if (user?.followers()?.pageInfo()?.hasNextPage() == true) user.followers().pageInfo().endCursor() else null
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
                    val followersQuery = FollowersQuery.builder()
                        .login(login)
                        .perPage(params.requestedLoadSize)
                        .before(params.key)
                        .build()

                    NetworkClient.apolloClient.query(followersQuery).toDeferred()
                }.await()

                val list = mutableListOf<UserGraphQL>()
                val user = response.data()?.user()

                user?.followers()?.nodes()?.forEach { node ->
                    node?.let {
                        list.add(UserGraphQL.createFromFollowerRaw(it) ?: return@let)
                    }
                }

                callback.onResult(
                    list,
                    if (user?.followers()?.pageInfo()?.hasPreviousPage() == true) user.followers().pageInfo().startCursor() else null
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