package io.github.tonnyl.moka.ui.commit

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.tonnyl.moka.data.CommitFile
import io.github.tonnyl.moka.data.CommitResponse
import io.github.tonnyl.moka.network.api.CommitApi
import io.github.tonnyl.moka.util.PageLinks
import io.github.tonnyl.moka.util.json
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import timber.log.Timber

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
                Timber.e(e)

                LoadResult.Error(e)
            }
        }
    }

}