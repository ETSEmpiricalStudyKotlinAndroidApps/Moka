package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams.Refresh
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullRepositoryItem
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryOwnedRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class OwnedRepositoriesDataSource(
    private val login: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<RepositoryItem>>>
) : PagingSource<String, RepositoryItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RepositoryItem> {
        val list = mutableListOf<RepositoryItem>()
        return withContext(Dispatchers.IO) {
            try {
                if (params is Refresh) {
                    initialLoadStatus.postValue(Resource.loading(null))
                }

                val user = queryOwnedRepositories(
                    login = login,
                    perPage = params.loadSize,
                    after = params.key,
                    before = params.key
                ).data()?.user

                user?.repositories?.nodes?.forEach { node ->
                    node?.let {
                        list.add(node.fragments.repositoryListItemFragment.toNonNullRepositoryItem())
                    }
                }

                val pageInfo = user?.repositories?.pageInfo?.fragments?.pageInfo

                Page(
                    data = list,
                    prevKey = pageInfo.checkedStartCursor,
                    nextKey = pageInfo.checkedEndCursor
                ).also {
                    if (params is Refresh) {
                        initialLoadStatus.postValue(Resource.success(list))
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)

                if (params is Refresh) {
                    initialLoadStatus.postValue(Resource.error(e.message, null))
                }

                Error<String, RepositoryItem>(e)
            }
        }
    }

}