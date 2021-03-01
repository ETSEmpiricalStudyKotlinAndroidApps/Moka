package io.github.tonnyl.moka.ui.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullRepositoryItem
import io.github.tonnyl.moka.network.queries.queryOwnedRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class OwnedRepositoriesDataSource(private val login: String) :
    PagingSource<String, RepositoryItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RepositoryItem> {
        val list = mutableListOf<RepositoryItem>()
        return withContext(Dispatchers.IO) {
            try {
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

}