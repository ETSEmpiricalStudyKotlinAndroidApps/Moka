package io.github.tonnyl.moka.ui.release.assets

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.fragment.ReleaseAsset
import io.github.tonnyl.moka.queries.ReleaseAssetsQuery
import io.github.tonnyl.moka.queries.ReleaseAssetsQuery.Data.Repository.Release.ReleaseAssets.PageInfo.Companion.pageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class ReleaseAssetsDataSource(
    private val apolloClient: ApolloClient,
    private val owner: String,
    private val name: String,
    private val tagName: String
) : PagingSource<String, ReleaseAsset>() {

    override fun getRefreshKey(state: PagingState<String, ReleaseAsset>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, ReleaseAsset> {
        val list = mutableListOf<ReleaseAsset>()
        return withContext(Dispatchers.IO) {
            try {
                val repository = apolloClient.query(
                    query = ReleaseAssetsQuery(
                        login = owner,
                        repoName = name,
                        tagName = tagName,
                        name = null,
                        after = params.key,
                        before = params.key,
                        perPage = params.loadSize
                    )
                ).data?.repository

                list.addAll(
                    repository?.release?.releaseAssets?.nodes.orEmpty().mapNotNull { it }
                )

                val pageInfo = repository?.release?.releaseAssets?.pageInfo?.pageInfo()

                LoadResult.Page(
                    data = list,
                    prevKey = pageInfo.checkedStartCursor,
                    nextKey = pageInfo.checkedEndCursor
                )
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                LoadResult.Error(e)
            }
        }
    }

}