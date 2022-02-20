package io.tonnyl.moka.common.network.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.tonnyl.moka.common.data.*
import io.tonnyl.moka.common.network.KtorClient
import kotlinx.datetime.Instant

class RepositoryApi(private val authenticatedKtorClient: HttpClient) {

    /**
     * Create a fork for the authenticated user.
     */
    suspend fun createAFork(
        owner: String,
        repo: String
    ) {
        return authenticatedKtorClient.post(
            urlString = "${KtorClient.GITHUB_V1_BASE_URL}/repos/$owner/$repo/forks"
        ) {
            header(HttpHeaders.Accept, "application/vnd.github.v3+json")
        }
    }

    /**
     * @param perPage Results per page (max 100).
     * @param page Page number of the results to fetch.
     * @param state Indicates the state of the issues to return.
     * @param since Only show notifications updated after the given time.
     * @param sort What to sort results by.
     */
    suspend fun issues(
        owner: String,
        repo: String,
        perPage: Int,
        page: Int,
        state: IssuePullRequestQueryState = IssuePullRequestQueryState.All,
        direction: Direction = Direction.Descending,
        sort: IssueQuerySort = IssueQuerySort.Created,
        since: Instant? = null
    ): HttpResponse {
        return authenticatedKtorClient.get(urlString = "${KtorClient.GITHUB_V1_BASE_URL}/repos/$owner/$repo/issues") {
            header("accept", "application/vnd.github.v3+json")
            parameter("state", state.rawValue)
            parameter("per_page", perPage.toString())
            parameter("page", page.toString())
            parameter("direction", direction.rawValue)
            parameter("sort", sort.rawValue)
            if (since != null) {
                parameter("since", since.toString())
            }
        }
    }

    suspend fun issuesByUrl(url: String): HttpResponse {
        return authenticatedKtorClient.get(urlString = url)
    }

    /**
     * @param perPage Results per page (max 100).
     * @param page Page number of the results to fetch.
     * @param state Indicates the state of the issues to return.
     * @param sort What to sort results by.
     */
    suspend fun pullRequests(
        owner: String,
        repo: String,
        perPage: Int,
        page: Int,
        state: IssuePullRequestQueryState = IssuePullRequestQueryState.All,
        direction: Direction = Direction.Descending,
        sort: PullRequestQuerySort = PullRequestQuerySort.Created,
    ): HttpResponse {
        return authenticatedKtorClient.get(urlString = "${KtorClient.GITHUB_V1_BASE_URL}/repos/$owner/$repo/pulls") {
            header("accept", "application/vnd.github.v3+json")
            parameter("state", state.rawValue)
            parameter("per_page", perPage.toString())
            parameter("page", page.toString())
            parameter("direction", direction.rawValue)
            parameter("sort", sort.rawValue)
        }
    }

    suspend fun pullRequestsByUrl(url: String): HttpResponse {
        return authenticatedKtorClient.get(urlString = url)
    }

    suspend fun releaseByUrl(url: String): ReleaseResponse {
        return authenticatedKtorClient.get(urlString = url)
    }

}