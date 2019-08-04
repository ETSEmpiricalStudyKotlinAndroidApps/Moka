package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.coroutines.toDeferred
import io.github.tonnyl.moka.UsersProjectsQuery
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.db.dao.ProjectsDao
import io.github.tonnyl.moka.network.NetworkClient
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.*
import timber.log.Timber

class ProjectsDataSource(
    private val coroutineScope: CoroutineScope,
    private val login: String,
    private val isMyself: Boolean,
    private val projectsDao: ProjectsDao,
    private val repositoryName: String?,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<Project>>>
) : PageKeyedDataSource<String, Project>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, Project>
    ) {
        Timber.d("loadInitial")

        if (login.isEmpty()) {
            return
        }

        loadStatusLiveData.postValue(PagedResource(initial = Resource.loading(null)))

        try {
            val projectsQuery = UsersProjectsQuery.builder()
                .owner(login)
                .perPage(params.requestedLoadSize)
                .build()

            val response = runBlocking {
                NetworkClient.apolloClient
                    .query(projectsQuery)
                    .execute()
            }

            val list = mutableListOf<Project>()
            val user = response.data()?.user()

            user?.projects()?.nodes()?.forEach { node ->
                list.add(Project.createFromRaw(node.fragments().project()))
            }

            if (isMyself && list.isNotEmpty()) {
                projectsDao.insert(list)
            }

            retry = null

            loadStatusLiveData.postValue(PagedResource(initial = Resource.success(list)))

            val pageInfo = user?.projects()?.pageInfo()

            callback.onResult(
                list,
                if (pageInfo?.hasPreviousPage() == true) pageInfo.startCursor() else null,
                if (pageInfo?.hasNextPage() == true) pageInfo.endCursor() else null
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadInitial(params, callback)
            }

            loadStatusLiveData.postValue(PagedResource(initial = Resource.error(e.message, null)))
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

                if (isMyself && list.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        projectsDao.insert(list)
                    }
                }

                retry = null

                loadStatusLiveData.value = PagedResource(after = Resource.success(list))

                val pageInfo = user?.projects()?.pageInfo()

                callback.onResult(
                    list,
                    if (pageInfo?.hasNextPage() == true) pageInfo.endCursor() else null
                )
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

                if (isMyself && list.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        projectsDao.insert(list)
                    }
                }

                retry = null

                loadStatusLiveData.value = PagedResource(after = Resource.success(list))

                val pageInfo = user?.projects()?.pageInfo()

                callback.onResult(
                    list,
                    if (pageInfo?.hasNextPage() == true) pageInfo.startCursor() else null
                )
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