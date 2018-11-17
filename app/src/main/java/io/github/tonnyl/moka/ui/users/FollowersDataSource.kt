package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.FollowersQuery
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.data.Status
import io.github.tonnyl.moka.data.UserGraphQL
import timber.log.Timber

class FollowersDataSource(private val login: String) : PageKeyedDataSource<String, UserGraphQL>() {

    val status = MutableLiveData<Status>()

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, UserGraphQL>) {
        Timber.d("loadInitial")

        status.postValue(Status.LOADING)

        val followerQuery = FollowersQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .build()

        val call = NetworkClient.apolloClient
                .query(followerQuery)

        try {
            // triggered by a refresh, we better execute sync
            val response = Rx2Apollo.from(call).blockingFirst()

            status.postValue(if (response.hasErrors().not()) Status.ERROR else Status.SUCCESS)

            val list = mutableListOf<UserGraphQL>()
            val user = response.data()?.user()

            user?.followers()?.nodes()?.forEach { node ->
                node?.let {
                    list.add(UserGraphQL.createFromFollowerRaw(node) ?: return@let)
                }
            }

            callback.onResult(list, user?.followers()?.pageInfo()?.startCursor(), user?.followers()?.pageInfo()?.endCursor())
        } catch (e: Exception) {
            status.postValue(Status.ERROR)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, UserGraphQL>) {
        Timber.d("loadAfter")

        val followersQuery = FollowersQuery.builder()
                .login(login)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

        NetworkClient.apolloClient
                .query(followersQuery)
                .enqueue(object : ApolloCall.Callback<FollowersQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e, "loadAfter error: ${e.message}")
                    }

                    override fun onResponse(response: Response<FollowersQuery.Data>) {
                        val list = mutableListOf<UserGraphQL>()
                        val user = response.data()?.user()

                        user?.followers()?.nodes()?.forEach { node ->
                            node?.let {
                                list.add(UserGraphQL.createFromFollowerRaw(it) ?: return@let)
                            }
                        }

                        callback.onResult(list, user?.followers()?.pageInfo()?.endCursor())
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

        NetworkClient.apolloClient
                .query(followersQuery)
                .enqueue(object : ApolloCall.Callback<FollowersQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e, "loadAfter error: ${e.message}")
                    }

                    override fun onResponse(response: Response<FollowersQuery.Data>) {
                        val list = mutableListOf<UserGraphQL>()
                        val user = response.data()?.user()

                        user?.followers()?.nodes()?.forEach { node ->
                            node?.let {
                                list.add(UserGraphQL.createFromFollowerRaw(it) ?: return@let)
                            }
                        }

                        callback.onResult(list, user?.followers()?.pageInfo()?.startCursor())
                    }

                })
    }

}