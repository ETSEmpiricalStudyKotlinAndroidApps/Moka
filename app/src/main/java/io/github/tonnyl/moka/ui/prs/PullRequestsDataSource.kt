package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.data.item.toNonNullPullRequestItem
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryPullRequests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PullRequestsDataSource(
    private val owner: String,
    private val name: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<PullRequestItem>>>
) : PagingSource<String, PullRequestItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PullRequestItem> {
        val list = mutableListOf<PullRequestItem>()
        return withContext(Dispatchers.IO) {
            try {
                if (params is LoadParams.Refresh) {
                    initialLoadStatus.postValue(Resource.loading(null))
                }

                val repository = queryPullRequests(
                    owner = owner,
                    name = name,
                    perPage = params.loadSize,
                    after = params.key,
                    before = params.key
                ).data()?.repository

                repository?.pullRequests?.nodes?.forEach { node ->
                    node?.let {
                        list.add(node.toNonNullPullRequestItem())
                    }
                }

                val pageInfo = repository?.pullRequests?.pageInfo?.fragments?.pageInfo

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

                LoadResult.Error<String, PullRequestItem>(e)
            }
        }
    }

}