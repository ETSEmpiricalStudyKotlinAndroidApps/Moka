package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.data.item.toNonNullProject
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryRepositoriesProjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class RepositoriesProjectsDataSource(
    private val login: String,
    private val repositoryName: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<Project>>>
) : PagingSource<String, Project>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Project> {
        val list = mutableListOf<Project>()

        return withContext(Dispatchers.IO) {
            try {
                if (params is LoadParams.Refresh) {
                    initialLoadStatus.postValue(Resource.loading(null))
                }

                val repository = queryRepositoriesProjects(
                    owner = login,
                    name = repositoryName,
                    perPage = params.loadSize
                ).data()?.repository

                repository?.projects?.nodes?.forEach { node ->
                    node?.let {
                        list.add(node.fragments.project.toNonNullProject())
                    }
                }

                val pageInfo = repository?.projects?.pageInfo?.fragments?.pageInfo

                LoadResult.Page(
                    data = list,
                    prevKey = pageInfo?.checkedStartCursor,
                    nextKey = pageInfo?.endCursor
                ).also {
                    if (params is LoadParams.Refresh) {
                        initialLoadStatus.postValue(Resource.success(list))
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)

                if (params is LoadParams.Refresh) {
                    initialLoadStatus.postValue(Resource.error(e.message, null))
                }

                LoadResult.Error<String, Project>(e)
            }
        }
    }

}