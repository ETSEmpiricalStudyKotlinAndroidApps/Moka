package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.MutableLiveData
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.data.item.toNonNullProject
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryRepositoriesProjects
import io.github.tonnyl.moka.ui.*

class RepositoriesProjectsDataSource(
    private val login: String,
    private val repositoryName: String,
    override val initial: MutableLiveData<Resource<List<Project>>>,
    override val previousOrNext: MutableLiveData<PagedResource<List<Project>>>
) : PageKeyedDataSourceWithLoadState<Project>() {

    override fun doLoadInitial(params: LoadInitialParams<String>): InitialLoadResponse<Project> {
        val response = queryRepositoriesProjects(
            owner = login,
            name = repositoryName,
            perPage = params.requestedLoadSize
        )

        val repo = response.data()?.repository

        val list = mutableListOf<Project>()

        repo?.projects?.nodes?.forEach { node ->
            node?.let {
                list.add(node.fragments.project.toNonNullProject())
            }
        }

        val pageInfo = repo?.projects?.pageInfo?.fragments?.pageInfo

        return InitialLoadResponse(
            list,
            PreviousPageKey(pageInfo.checkedStartCursor),
            NextPageKey(pageInfo.checkedStartCursor)
        )
    }

    override fun doLoadAfter(params: LoadParams<String>): AfterLoadResponse<Project> {
        val response = queryRepositoriesProjects(
            owner = login,
            name = repositoryName,
            perPage = params.requestedLoadSize,
            after = params.key
        )

        val repo = response.data()?.repository

        val list = mutableListOf<Project>()

        repo?.projects?.nodes?.forEach { node ->
            node?.let {
                list.add(node.fragments.project.toNonNullProject())
            }
        }

        val pageInfo = repo?.projects?.pageInfo?.fragments?.pageInfo

        return AfterLoadResponse(list, NextPageKey(pageInfo.checkedEndCursor))
    }

    override fun doLoadBefore(params: LoadParams<String>): BeforeLoadResponse<Project> {
        val response = queryRepositoriesProjects(
            owner = login,
            name = repositoryName,
            perPage = params.requestedLoadSize,
            before = params.key
        )

        val repo = response.data()?.repository

        val list = mutableListOf<Project>()

        repo?.projects?.nodes?.forEach { node ->
            node?.let {
                list.add(node.fragments.project.toNonNullProject())
            }
        }

        val pageInfo = repo?.projects?.pageInfo?.fragments?.pageInfo

        return BeforeLoadResponse(list, PreviousPageKey(pageInfo.checkedStartCursor))
    }

}