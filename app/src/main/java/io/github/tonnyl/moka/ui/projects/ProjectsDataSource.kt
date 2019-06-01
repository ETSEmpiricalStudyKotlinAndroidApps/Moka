package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.UsersProjectsQuery
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import timber.log.Timber

class ProjectsDataSource(
        private val login: String,
        private val repositoryName: String?,
        private val loadStatusLiveData: MutableLiveData<PagedResource<List<Project>>>
) : PageKeyedDataSource<String, Project>() {

    var retry: (() -> Any)? = null

    private var apolloCall: ApolloQueryCall<UsersProjectsQuery.Data>? = null


    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Project>) {
        Timber.d("loadInitial")

        loadStatusLiveData.postValue(PagedResource(initial = Resource.loading(null)))

        val projectsQuery = UsersProjectsQuery.builder()
                .owner(login)
                .perPage(params.requestedLoadSize)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(projectsQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<UsersProjectsQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadInitial(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(initial = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<UsersProjectsQuery.Data>) {
                val list = mutableListOf<Project>()
                val user = response.data()?.user()

                user?.projects()?.nodes()?.forEach { node ->
                    list.add(Project.createFromRaw(node.fragments().project()))
                }

                val pageInfo = user?.projects()?.pageInfo()

                callback.onResult(
                        list,
                        if (pageInfo?.hasPreviousPage() == true) pageInfo.startCursor() else null,
                        if (pageInfo?.hasNextPage() == true) pageInfo.endCursor() else null
                )

                retry = null

                loadStatusLiveData.postValue(PagedResource(initial = Resource.success(list)))
            }

        })
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Project>) {
        Timber.d("loadAfter")

        loadStatusLiveData.postValue(PagedResource(after = Resource.loading(null)))

        val projectsQuery = UsersProjectsQuery.builder()
                .owner(login)
                .after(params.key)
                .perPage(params.requestedLoadSize)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(projectsQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<UsersProjectsQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(after = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<UsersProjectsQuery.Data>) {
                val list = mutableListOf<Project>()
                val user = response.data()?.user()

                user?.projects()?.nodes()?.forEach { node ->
                    list.add(Project.createFromRaw(node.fragments().project()))
                }

                val pageInfo = user?.projects()?.pageInfo()

                callback.onResult(list, if (pageInfo?.hasNextPage() == true) pageInfo.endCursor() else null)

                retry = null

                loadStatusLiveData.postValue(PagedResource(after = Resource.success(list)))
            }

        })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Project>) {
        Timber.d("loadBefore")

        loadStatusLiveData.postValue(PagedResource(before = Resource.loading(null)))

        val projectsQuery = UsersProjectsQuery.builder()
                .owner(login)
                .before(params.key)
                .perPage(params.requestedLoadSize)
                .build()

        apolloCall = NetworkClient.apolloClient
                .query(projectsQuery)

        apolloCall?.enqueue(object : ApolloCall.Callback<UsersProjectsQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(after = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<UsersProjectsQuery.Data>) {
                val list = mutableListOf<Project>()
                val user = response.data()?.user()

                user?.projects()?.nodes()?.forEach { node ->
                    list.add(Project.createFromRaw(node.fragments().project()))
                }

                val pageInfo = user?.projects()?.pageInfo()

                callback.onResult(list, if (pageInfo?.hasNextPage() == true) pageInfo.startCursor() else null)

                retry = null

                loadStatusLiveData.postValue(PagedResource(after = Resource.success(list)))
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