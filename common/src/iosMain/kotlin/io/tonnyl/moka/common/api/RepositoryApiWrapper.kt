package io.tonnyl.moka.common.api

import io.ktor.client.statement.*
import io.tonnyl.moka.common.data.*
import io.tonnyl.moka.common.network.PageLinks
import io.tonnyl.moka.common.network.api.RepositoryApi
import io.tonnyl.moka.common.serialization.json
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString

@Suppress("unused")
class RepositoryApiWrapper(private val api: RepositoryApi) {

    suspend fun issues(
        owner: String,
        repo: String,
        perPage: Int,
        page: Int,
        state: IssuePullRequestQueryState = IssuePullRequestQueryState.All,
        direction: Direction = Direction.Descending,
        sort: IssueQuerySort = IssueQuerySort.Created,
        since: Instant? = null
    ): Result<Pair<List<IssueListItem>, PageLinks>> {
        return try {
            Result.success(
                value = api.issues(
                    owner = owner,
                    repo = repo,
                    perPage = perPage,
                    page = page,
                    state = state,
                    direction = direction,
                    sort = sort,
                    since = since
                ).decodeIssuesResp()
            )
        } catch (e: Exception) {
            Result.failure(error = e)
        }
    }

    suspend fun issuesByUrl(url: String): Result<Pair<List<IssueListItem>, PageLinks>> {
        return try {
            Result.success(value = api.issuesByUrl(url = url).decodeIssuesResp())
        } catch (e: Exception) {
            Result.failure(error = e)
        }
    }

    suspend fun pullRequests(
        owner: String,
        repo: String,
        perPage: Int,
        page: Int,
        state: IssuePullRequestQueryState = IssuePullRequestQueryState.All,
        direction: Direction = Direction.Descending,
        sort: PullRequestQuerySort = PullRequestQuerySort.Created,
    ): Result<Pair<List<PullRequestListItem>, PageLinks>> {
        return try {
            Result.success(
                value = api.pullRequests(
                    owner = owner,
                    repo = repo,
                    perPage = perPage,
                    page = page,
                    state = state,
                    direction = direction,
                    sort = sort
                ).decodePrsResp()
            )
        } catch (e: Exception) {
            Result.failure(error = e)
        }
    }

    suspend fun pullRequestsByUrl(url: String): Result<Pair<List<PullRequestListItem>, PageLinks>> {
        return try {
            Result.success(
                value = api.pullRequestsByUrl(url = url).decodePrsResp()
            )
        } catch (e: Exception) {
            Result.failure(error = e)
        }
    }

    private suspend fun HttpResponse.decodeIssuesResp(): Pair<List<IssueListItem>, PageLinks> {
        val data = json.decodeFromString<List<IssueListItem>>(string = readText())
        val pl = PageLinks(this)

        return Pair(data, pl)
    }

    private suspend fun HttpResponse.decodePrsResp(): Pair<List<PullRequestListItem>, PageLinks> {
        val data = json.decodeFromString<List<PullRequestListItem>>(string = readText())
        val pl = PageLinks(this)

        return Pair(data, pl)
    }

}