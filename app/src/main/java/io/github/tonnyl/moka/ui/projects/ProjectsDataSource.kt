package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.MutableLiveData
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.data.item.toNonNullProject
import io.github.tonnyl.moka.db.dao.ProjectsDao
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryUsersProjects
import io.github.tonnyl.moka.ui.*

class ProjectsDataSource(
    private val login: String,
    private val isMyself: Boolean,
    private val projectsDao: ProjectsDao,
    private val repositoryName: String?,
    override val initial: MutableLiveData<Resource<List<Project>>>,
    override val previousOrNext: MutableLiveData<PagedResource<List<Project>>>
) : PageKeyedDataSourceWithLoadState<Project>() {

    override fun doLoadInitial(params: LoadInitialParams<String>): InitialLoadResponse<Project> {
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

        val pageInfo = user?.projects?.pageInfo?.fragments?.pageInfo

        return InitialLoadResponse(
            list,
            PreviousPageKey(pageInfo.checkedStartCursor),
            NextPageKey(pageInfo.checkedEndCursor)
        )
    }

    override fun doLoadAfter(params: LoadParams<String>): AfterLoadResponse<Project> {
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

        return AfterLoadResponse(
            list,
            NextPageKey(user?.projects?.pageInfo?.fragments?.pageInfo.checkedEndCursor)
        )
    }

    override fun doLoadBefore(params: LoadParams<String>): BeforeLoadResponse<Project> {
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

        return BeforeLoadResponse(
            list,
            PreviousPageKey(user?.projects?.pageInfo?.fragments?.pageInfo.checkedStartCursor)
        )
    }

}