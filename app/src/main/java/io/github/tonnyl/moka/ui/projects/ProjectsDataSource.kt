package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.coroutines.toDeferred
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.UsersProjectsQuery
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ProjectsDataSource(
    private val coroutineScope: CoroutineScope,
    private val login: String,
    private val repositoryName: String?,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<Project>>>
) : PageKeyedDataSource<String, Project>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Project>) {
        Timber.d("loadInitial")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(initial = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val projectsQuery = UsersProjectsQuery.builder()
                        .owner(login)
                        .perPage(params.requestedLoadSize)
                        .build()

                    NetworkClient.apolloClient.query(projectsQuery).toDeferred()
                }.await()

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

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Project>) {
        Timber.d("loadAfter")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(after = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val projectsQuery = UsersProjectsQuery.builder()
                        .owner(login)
                        .after(params.key)
                        .perPage(params.requestedLoadSize)
                        .build()

                    NetworkClient.apolloClient.query(projectsQuery).toDeferred()
                }.await()

                val list = mutableListOf<Project>()
                val user = response.data()?.user()

                user?.projects()?.nodes()?.forEach { node ->
                    list.add(Project.createFromRaw(node.fragments().project()))
                }

                val pageInfo = user?.projects()?.pageInfo()

                callback.onResult(list, if (pageInfo?.hasNextPage() == true) pageInfo.endCursor() else null)

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

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Project>) {
        Timber.d("loadBefore")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(before = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val projectsQuery = UsersProjectsQuery.builder()
                        .owner(login)
                        .before(params.key)
                        .perPage(params.requestedLoadSize)
                        .build()

                    NetworkClient.apolloClient.query(projectsQuery).toDeferred()
                }.await()

                val list = mutableListOf<Project>()
                val user = response.data()?.user()

                user?.projects()?.nodes()?.forEach { node ->
                    list.add(Project.createFromRaw(node.fragments().project()))
                }

                val pageInfo = user?.projects()?.pageInfo()

                callback.onResult(list, if (pageInfo?.hasNextPage() == true) pageInfo.startCursor() else null)

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

}