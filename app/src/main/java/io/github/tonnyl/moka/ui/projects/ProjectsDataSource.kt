package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.data.item.toNonNullProject
import io.github.tonnyl.moka.db.dao.ProjectsDao
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.queries.UsersProjectsQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class ProjectsDataSource(
    private val login: String,
    private val isMyself: Boolean,
    private val projectsDao: ProjectsDao,
    private val repositoryName: String?,
    private val initialLoadStatusLiveData: MutableLiveData<Resource<List<Project>>>,
    private val previousNextStatusLiveData: MutableLiveData<PagedResource2<List<Project>>>
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

        initialLoadStatusLiveData.postValue(Resource.loading(null))

        try {
            val projectsQuery = UsersProjectsQuery(
                login,
                Input.absent(),
                Input.absent(),
                params.requestedLoadSize
            )

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(projectsQuery)
                    .execute()
            }

            val list = mutableListOf<Project>()
            val user = response.data()?.user

            user?.projects?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.fragments.project.toNonNullProject())
                }
            }

            if (isMyself && list.isNotEmpty()) {
                projectsDao.insert(list)
            }

            retry = null

            initialLoadStatusLiveData.postValue(Resource.success(list))

            val pageInfo = user?.projects?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                if (pageInfo?.hasPreviousPage == true) {
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
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadInitial(params, callback)
            }

            initialLoadStatusLiveData.postValue(Resource.error(e.message, null))
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Project>) {
        Timber.d("loadAfter")

        previousNextStatusLiveData.postValue(
            PagedResource2(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val response = runBlocking {
                val projectsQuery = UsersProjectsQuery(
                    login,
                    Input.fromNullable(params.key),
                    Input.absent(),
                    params.requestedLoadSize
                )

                GraphQLClient.apolloClient
                    .query(projectsQuery)
                    .execute()
            }

            val list = mutableListOf<Project>()
            val user = response.data()?.user

            user?.projects?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.fragments.project.toNonNullProject())
                }
            }

            if (isMyself && list.isNotEmpty()) {
                projectsDao.insert(list)
            }

            retry = null

            previousNextStatusLiveData.postValue(
                PagedResource2(PagedResourceDirection.AFTER, Resource.success(list))
            )

            val pageInfo = user?.projects?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                if (pageInfo?.hasNextPage == true) {
                    pageInfo.endCursor
                } else {
                    null
                }
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadAfter(params, callback)
            }

            previousNextStatusLiveData.postValue(
                PagedResource2(PagedResourceDirection.AFTER, Resource.error(e.message, null))
            )
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Project>) {
        Timber.d("loadBefore")

        previousNextStatusLiveData.postValue(
            PagedResource2(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val response = runBlocking {
                val projectsQuery = UsersProjectsQuery(
                    login,
                    Input.absent(),
                    Input.fromNullable(params.key),
                    params.requestedLoadSize
                )

                GraphQLClient.apolloClient
                    .query(projectsQuery)
                    .execute()
            }

            val list = mutableListOf<Project>()
            val user = response.data()?.user

            user?.projects?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.fragments.project.toNonNullProject())
                }
            }

            if (isMyself && list.isNotEmpty()) {
                projectsDao.insert(list)
            }

            retry = null

            previousNextStatusLiveData.postValue(
                PagedResource2(PagedResourceDirection.BEFORE, Resource.success(list))
            )

            val pageInfo = user?.projects?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                if (pageInfo?.hasNextPage == true) {
                    pageInfo.startCursor
                } else {
                    null
                }
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadAfter(params, callback)
            }

            previousNextStatusLiveData.postValue(
                PagedResource2(PagedResourceDirection.BEFORE, Resource.error(e.message, null))
            )
        }
    }

}