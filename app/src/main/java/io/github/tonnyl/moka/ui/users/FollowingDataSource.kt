package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.FollowingQuery
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.data.UserGraphQL
import io.github.tonnyl.moka.net.Status
import timber.log.Timber

class FollowingDataSource(private val login: String) : PageKeyedDataSource<String, UserGraphQL>() {

    val status = MutableLiveData<Status>()

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, UserGraphQL>) {
        Timber.d("loadInitial")

        status.postValue(Status.LOADING)

        val followingQuery = FollowingQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .build()

        val call = NetworkClient.apolloClient
                .query(followingQuery)

        try {
            // triggered by a refresh, we better execute sync
            val response = Rx2Apollo.from(call).blockingFirst()

            status.postValue(if (response.hasErrors().not()) Status.ERROR else Status.SUCCESS)

            val list = mutableListOf<UserGraphQL>()
            val user = response.data()?.user()

            user?.following()?.nodes()?.forEach { node ->
                node?.let {
                    list.add(UserGraphQL.createFromFollowingRaw(node) ?: return@let)
                }
            }

            callback.onResult(list, if (user?.following()?.pageInfo()?.hasPreviousPage() == true) user.following().pageInfo().startCursor() else null, if (user?.following()?.pageInfo()?.hasNextPage() == true) user.following().pageInfo().endCursor() else null)
        } catch (e: Exception) {
            status.postValue(Status.ERROR)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, UserGraphQL>) {
        Timber.d("loadAfter")

        val followingQuery = FollowingQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

        NetworkClient.apolloClient
                .query(followingQuery)
                .enqueue(object : ApolloCall.Callback<FollowingQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e, "loadAfter error: ${e.message}")
                    }

                    override fun onResponse(response: Response<FollowingQuery.Data>) {
                        val list = mutableListOf<UserGraphQL>()
                        val user = response.data()?.user()

                        user?.following()?.nodes()?.forEach { node ->
                            node?.let {
                                list.add(UserGraphQL.createFromFollowingRaw(it) ?: return@let)
                            }
                        }

                        callback.onResult(list, if (user?.following()?.pageInfo()?.hasNextPage() == true) user.following().pageInfo().endCursor() else null)
                    }

                })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, UserGraphQL>) {
        Timber.d("loadBefore")

        val followersQuery = FollowingQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .before(params.key)
                .build()

        NetworkClient.apolloClient
                .query(followersQuery)
                .enqueue(object : ApolloCall.Callback<FollowingQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e, "loadAfter error: ${e.message}")
                    }

                    override fun onResponse(response: Response<FollowingQuery.Data>) {
                        val list = mutableListOf<UserGraphQL>()
                        val user = response.data()?.user()

                        user?.following()?.nodes()?.forEach { node ->
                            node?.let {
                                list.add(UserGraphQL.createFromFollowingRaw(it) ?: return@let)
                            }
                        }

                        callback.onResult(list, if (user?.following()?.pageInfo()?.hasPreviousPage() == true) user.following().pageInfo().startCursor() else null)
                    }

                })
    }
}