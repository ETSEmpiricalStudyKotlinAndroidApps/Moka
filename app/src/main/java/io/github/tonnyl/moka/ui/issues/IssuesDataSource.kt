package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.data.item.toNonNullIssueItem
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryIssues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class IssuesDataSource(
    private val owner: String,
    private val name: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<IssueItem>>>
) : PagingSource<String, IssueItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, IssueItem> {
        val list = mutableListOf<IssueItem>()
        return withContext(Dispatchers.IO) {
            try {
                if (params is LoadParams.Refresh) {
                    initialLoadStatus.postValue(Resource.loading(null))
                }

                val repository = queryIssues(
                    owner = owner,
                    name = name,
                    perPage = params.loadSize,
                    after = params.key,
                    before = params.key
                ).data()?.repository

                repository?.issues?.nodes?.forEach { node ->
                    node?.let {
                        list.add(node.toNonNullIssueItem())
                    }
                }

                val pageInfo = repository?.issues?.pageInfo?.fragments?.pageInfo

                LoadResult.Page(
                    data = list,
                    prevKey = pageInfo.checkedStartCursor,
                    nextKey = pageInfo.checkedEndCursor
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

                LoadResult.Error<String, IssueItem>(e)
            }
        }
    }

}