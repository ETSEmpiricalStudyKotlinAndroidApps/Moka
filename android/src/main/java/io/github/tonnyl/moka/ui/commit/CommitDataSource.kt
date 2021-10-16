package io.github.tonnyl.moka.ui.commit

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.tonnyl.moka.data.CommitFile
import io.github.tonnyl.moka.data.CommitResponse
import io.ktor.client.statement.*
import io.tonnyl.moka.common.network.api.CommitApi
import io.tonnyl.moka.common.network.PageLinks
import io.tonnyl.moka.common.serialization.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

@ExperimentalSerializationApi
class CommitDataSource(
    private val initialResp: MutableLiveData<CommitResponse>,
    private val commitApi: CommitApi,
    private val owner: String,
    private val repo: String,
    private val ref: String
) : PagingSource<String, CommitFile>() {

    override fun getRefreshKey(state: PagingState<String, CommitFile>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, CommitFile> {
        return withContext(Dispatchers.IO) {
            val list = mutableListOf<CommitFile>()

            try {
                val isRefresh = params is LoadParams.Refresh
                val httpResp = if (isRefresh) {
                    commitApi.getACommit(
                        owner = owner,
                        repo = repo,
                        ref = ref,
                        page = 1,
                        perPage = params.loadSize
                    )
                } else {
                    val key = params.key
                    if (key.isNullOrEmpty()) {
                        return@withContext LoadResult.Page(
                            data = list,
                            prevKey = null,
                            nextKey = null
                        )
                    } else {
                        commitApi.getACommitByUrl(key)
                    }
                }

                val commitResp = json.decodeFromString<CommitResponse>(string = httpResp.readText())

                if (isRefresh) {
                    initialResp.postValue(commitResp)
                }

                list.addAll(commitResp.files)

                val pl = PageLinks(httpResp)

                LoadResult.Page(
                    data = list,
                    prevKey = pl.prev,
                    nextKey = pl.next
                )
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                LoadResult.Error(e)
            }
        }
    }

}