package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.data.item.toNonNullProject
import io.github.tonnyl.moka.db.dao.ProjectsDao
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryUsersProjects
import timber.log.Timber

class ProjectsDataSource(
    private val login: String,
    private val isMyself: Boolean,
    private val projectsDao: ProjectsDao,
    private val repositoryName: String?,
    private val initialLoadStatusLiveData: MutableLiveData<Resource<List<Project>>>,
    private val previousNextStatusLiveData: MutableLiveData<PagedResource<List<Project>>>
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
            val response = queryUsersProjects(
                owner = login,
                perPage = params.requestedLoadSize
            )

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
                pageInfo.checkedStartCursor,
                pageInfo.checkedEndCursor
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
            PagedResource(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val response = queryUsersProjects(
                owner = login,
                after = params.key,
                perPage = params.requestedLoadSize
            )

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
                PagedResource(PagedResourceDirection.AFTER, Resource.success(list))
            )

            callback.onResult(
                list,
                user?.projects?.pageInfo?.fragments?.pageInfo.checkedEndCursor
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadAfter(params, callback)
            }

            previousNextStatusLiveData.postValue(
                PagedResource(PagedResourceDirection.AFTER, Resource.error(e.message, null))
            )
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Project>) {
        Timber.d("loadBefore")

        previousNextStatusLiveData.postValue(
            PagedResource(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val response = queryUsersProjects(
                owner = login,
                before = params.key,
                perPage = params.requestedLoadSize
            )

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
                PagedResource(PagedResourceDirection.BEFORE, Resource.success(list))
            )

            callback.onResult(
                list,
                user?.projects?.pageInfo?.fragments?.pageInfo.checkedStartCursor
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadAfter(params, callback)
            }

            previousNextStatusLiveData.postValue(
                PagedResource(PagedResourceDirection.BEFORE, Resource.error(e.message, null))
            )
        }
    }

}