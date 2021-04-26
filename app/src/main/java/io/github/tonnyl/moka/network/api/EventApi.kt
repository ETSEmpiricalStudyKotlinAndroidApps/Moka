package io.github.tonnyl.moka.network.api

import io.github.tonnyl.moka.network.KtorClient.Companion.GITHUB_V1_BASE_URL
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class EventApi(private val ktorClient: HttpClient) {

    /**
     * These are events that you've received by watching repos and following users.
     * If you are authenticated as the given user, you will see private events.
     * Otherwise, you'll only see public events.
     */
    suspend fun listPublicEventThatAUserHasReceived(
        username: String,
        page: Int,
        perPage: Int
    ): HttpResponse {
        return ktorClient.get(
            urlString = "$GITHUB_V1_BASE_URL/users/${username}/received_events/public?page=${page}&per_page=${perPage}"
        )
    }

    /**
     * @see [listPublicEventThatAUserHasReceived]
     */
    suspend fun listPublicEventThatAUserHasReceivedByUrl(url: String): HttpResponse {
        return ktorClient.get(url)
    }

}