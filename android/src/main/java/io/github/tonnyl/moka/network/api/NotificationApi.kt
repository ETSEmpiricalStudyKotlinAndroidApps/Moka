package io.github.tonnyl.moka.network.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.tonnyl.moka.common.network.KtorClient.Companion.GITHUB_V1_BASE_URL

class NotificationApi(private val ktorClient: HttpClient) {

    /**
     * List all notifications for the current user, sorted by most recently updated.
     *
     * @param all If true, show notifications marked as read. Default: true.
     */
    suspend fun listNotifications(
        all: Boolean = true,
        page: Int,
        perPage: Int
    ): HttpResponse {
        return ktorClient.get(urlString = "$GITHUB_V1_BASE_URL/notifications?all=${all}&page=${page}&per_page=${perPage}")
    }

    /**
     * @see [listNotifications]
     */
    suspend fun listNotificationsByUrl(url: String): HttpResponse {
        return ktorClient.get(urlString = url)
    }

    /**
     * Marking a notification as "read" removes it from the default view on GitHub.
     *
     * @param lastReadAt Describes the last point that notifications were checked. Anything updated since this time will not be updated.
     * This is a timestamp in ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ. Default: Time.now
     */
    suspend fun markAsRead(lastReadAt: String) {
        return ktorClient.put(urlString = "$GITHUB_V1_BASE_URL/notifications?last_read_at=${lastReadAt}")
    }

    /**
     * Mutes all future notifications for a conversation until you comment on the thread or get @mentioned.
     *
     * @param threadId thread id of notification.
     */
    suspend fun deleteAThreadSubscription(threadId: String) {
        ktorClient.delete<Unit>(urlString = "$GITHUB_V1_BASE_URL/notifications/threads/${threadId}/subscription")
    }

}