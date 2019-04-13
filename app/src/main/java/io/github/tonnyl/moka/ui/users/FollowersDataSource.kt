package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.github.tonnyl.moka.FollowersQuery
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.data.PagedResource
import io.github.tonnyl.moka.data.UserGraphQL
import io.github.tonnyl.moka.net.Resource
import timber.log.Timber

class FollowersDataSource(
        private val login: String,
        private val loadStatusLiveData: MutableLiveData<PagedResource<List<UserGraphQL>>>
) : PageKeyedDataSource<String, UserGraphQL>() {

    var retry: (() -> Any)? = null

    private var apolloCall: ApolloQueryCall<FollowersQuery.Data>? = null

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, UserGraphQL>) {
        Timber.d("loadInitial")

        loadStatusLiveData.postValue(PagedResource(initial = Resource.loading(null)))

        val followerQuery = FollowersQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(followerQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<FollowersQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadInitial(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(initial = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<FollowersQuery.Data>) {
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

                loadStatusLiveData.postValue(PagedResource(initial = Resource.success(list)))
            }

        })
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, UserGraphQL>) {
        Timber.d("loadAfter")

        loadStatusLiveData.postValue(PagedResource(after = Resource.loading(null)))

        val followersQuery = FollowersQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(followersQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<FollowersQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(after = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<FollowersQuery.Data>) {
                val list = mutableListOf<UserGraphQL>()
                val user = response.data()?.user()

                user?.followers()?.nodes()?.forEach { node ->
                    node?.let {
                        list.add(UserGraphQL.createFromFollowerRaw(it) ?: return@let)
                    }
                }

                callback.onResult(list, if (user?.followers()?.pageInfo()?.hasNextPage() == true) user.followers().pageInfo().endCursor() else null)

                retry = null

                loadStatusLiveData.postValue(PagedResource(after = Resource.success(list)))
            }

        })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, UserGraphQL>) {
        Timber.d("loadBefore")

        val followersQuery = FollowersQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .before(params.key)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(followersQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<FollowersQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadBefore(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(before = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<FollowersQuery.Data>) {
                val list = mutableListOf<UserGraphQL>()
                val user = response.data()?.user()

                user?.followers()?.nodes()?.forEach { node ->
                    node?.let {
                        list.add(UserGraphQL.createFromFollowerRaw(it) ?: return@let)
                    }
                }

                callback.onResult(list, if (user?.followers()?.pageInfo()?.hasPreviousPage() == true) user.followers().pageInfo().startCursor() else null)

                retry = null

                loadStatusLiveData.postValue(PagedResource(before = Resource.success(list)))
            }

        })
    }

    override fun invalidate() {
        super.invalidate()

        if (apolloCall?.isCanceled == false) {
            apolloCall?.cancel()
        }
    }

}