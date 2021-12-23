package io.github.tonnyl.moka.ui.prs

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.ktor.client.statement.*
import io.tonnyl.moka.common.data.IssuePullRequestQueryState
import io.tonnyl.moka.common.data.PullRequestListItem
import io.tonnyl.moka.common.network.PageLinks
import io.tonnyl.moka.common.network.api.RepositoryApi
import io.tonnyl.moka.common.serialization.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class PullRequestsDataSource(
    private val api: RepositoryApi,
    private val owner: String,
    private val name: String,
    private val queryState: IssuePullRequestQueryState
) : PagingSource<String, PullRequestListItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PullRequestListItem> {
        return withContext(Dispatchers.IO) {
            try {
                val isRefresh = params is LoadParams.Refresh
                val response = if (isRefresh) {
                    api.pullRequests(
                        owner = owner,
                        repo = name,
                        perPage = params.loadSize,
                        page = params.key?.toInt() ?: 1,
                        state = queryState
                    )
                } else {
                    val key = params.key
                    if (key.isNullOrEmpty()) {
                        return@withContext LoadResult.Page(
                            data = emptyList(),
                            prevKey = null,
                            nextKey = null
                        )
                    } else {
                        api.pullRequestsByUrl(url = key)
                    }
                }

                val pullRequests =
                    json.decodeFromString<List<PullRequestListItem>>(string = response.bodyAsText())

                val pl = PageLinks(response)

                LoadResult.Page(
                    data = pullRequests,
                    prevKey = pl.prev,
                    nextKey = pl.next
                )
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<String, PullRequestListItem>): String? {
        return null
    }

}