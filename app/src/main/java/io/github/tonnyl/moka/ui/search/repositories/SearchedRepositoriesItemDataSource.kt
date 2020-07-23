package io.github.tonnyl.moka.ui.search.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams.Refresh
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem
import io.github.tonnyl.moka.data.item.toNonNullSearchedRepositoryItem
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.querySearchRepositories
import io.github.tonnyl.moka.queries.SearchRepositoriesQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SearchedRepositoriesPagingSource(
    val query: String,
    val initialLoadStatus: MutableLiveData<Resource<List<SearchedRepositoryItem>>>
) : PagingSource<String, SearchedRepositoryItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, SearchedRepositoryItem> {
        val list = mutableListOf<SearchedRepositoryItem>()

        return withContext(Dispatchers.IO) {
            try {
                if (params is Refresh) {
                    initialLoadStatus.postValue(Resource.loading(null))
                }

                val search = querySearchRepositories(
                    queryWords = query,
                    first = params.loadSize,
                    after = params.key,
                    before = params.key
                ).data()?.search

                search?.nodes?.forEach { node ->
                    node?.let {
                        convertRawDataToSearchedRepositoryItem(node)?.let {
                            list.add(it)
                        }
                    }
                }

                val pageInfo = search?.pageInfo?.fragments?.pageInfo
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

                Error<String, SearchedRepositoryItem>(e)
            }
        }
    }

    private fun convertRawDataToSearchedRepositoryItem(
        node: SearchRepositoriesQuery.Node
    ): SearchedRepositoryItem? {
        return node.fragments.repositoryListItemFragment?.toNonNullSearchedRepositoryItem()
    }

}