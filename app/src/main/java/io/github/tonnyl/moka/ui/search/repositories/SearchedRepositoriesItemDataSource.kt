package io.github.tonnyl.moka.ui.search.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullRepositoryItem
import io.github.tonnyl.moka.network.queries.querySearchRepositories
import io.github.tonnyl.moka.queries.SearchRepositoriesQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SearchedRepositoriesItemDataSource(
    private val query: String
) : PagingSource<String, RepositoryItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RepositoryItem> {
        val list = mutableListOf<RepositoryItem>()

        return withContext(Dispatchers.IO) {
            try {
                val search = querySearchRepositories(
                    queryWords = query,
                    first = params.loadSize,
                    after = params.key,
                    before = params.key
                ).data()?.search

                search?.nodes?.forEach { node ->
                    node?.let {
                        convertRawDataRepositoryItem(node)?.let {
                            list.add(it)
                        }
                    }
                }

                val pageInfo = search?.pageInfo?.fragments?.pageInfo
                Page(
                    data = list,
                    prevKey = pageInfo.checkedStartCursor,
                    nextKey = pageInfo.checkedEndCursor
                )
            } catch (e: Exception) {
                Timber.e(e)

                Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<String, RepositoryItem>): String? {
        return null
    }

    private fun convertRawDataRepositoryItem(
        node: SearchRepositoriesQuery.Node
    ): RepositoryItem? {
        return node.fragments.repositoryListItemFragment?.toNonNullRepositoryItem()
    }

}