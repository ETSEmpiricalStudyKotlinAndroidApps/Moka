package io.tonnyl.moka.common.api

import io.ktor.client.statement.*
import io.tonnyl.moka.common.data.CommitFile
import io.tonnyl.moka.common.network.PageLinks
import io.tonnyl.moka.common.network.api.CommitApi
import io.tonnyl.moka.common.serialization.json
import kotlinx.serialization.decodeFromString

@Suppress("unused")
class CommitApiWrapper(private val api: CommitApi) {

    suspend fun getACommit(
        owner: String,
        repo: String,
        ref: String,
        page: Int,
        perPage: Int
    ): Result<Pair<List<CommitFile>, PageLinks>> {
        return try {
            Result.success(
                value = api.getACommit(
                    owner = owner,
                    repo = repo,
                    ref = ref,
                    page = page,
                    perPage = perPage
                ).decodeResp()
            )
        } catch (e: Exception) {
            Result.failure(error = e)
        }
    }

    suspend fun getACommitByUrl(url: String): Result<Pair<List<CommitFile>, PageLinks>> {
        return try {
            Result.success(
                value = api.getACommitByUrl(url = url).decodeResp()
            )
        } catch (e: Exception) {
            Result.failure(error = e)
        }
    }

    private suspend fun HttpResponse.decodeResp(): Pair<List<CommitFile>, PageLinks> {
        val data = json.decodeFromString<List<CommitFile>>(string = readText())
        val pl = PageLinks(this)

        return Pair(data, pl)
    }

}