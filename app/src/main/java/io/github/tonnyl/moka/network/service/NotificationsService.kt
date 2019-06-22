package io.github.tonnyl.moka.network.service

import io.github.tonnyl.moka.data.Notification
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface NotificationsService {

    /**
     * List all notifications for the current user, sorted by most recently updated.
     *
     * @param all If true, show notifications marked as read. Default: true.
     *
     * @return If successful, return a list of [Notification].
     */
    @GET("notifications")
    suspend fun listNotifications(
        @Query("all") all: Boolean = true,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<List<Notification>>

    /**
     * Refer [listNotifications].
     */
    @GET
    suspend fun listNotificationsByUrl(
        @Url url: String
    ): Response<List<Notification>>

    /**
     * Marking a notification as "read" removes it from the default view on GitHub.
     *
     * @param lastReadAt Describes the last point that notifications were checked. Anything updated since this time will not be updated.
     * This is a timestamp in ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ. Default: Time.now
     *
     * @return If successful, return a [Unit] wrapped by a [Call].
     */
    @PUT("notifications")
    suspend fun markAsRead(
        @Query("last_read_at") lastReadAt: String
    ): Response<Unit>

    /**
     * Mutes all future notifications for a conversation until you comment on the thread or get @mentioned.
     *
     * @param threadId thread id of notification.
     *
     * @return If successful, return a [Unit] wrapped by [Call].
     */
    @DELETE("notifications/threads/{thread_id}/subscription")
    suspend fun deleteAThreadSubscription(
        @Path("thread_id") threadId: String
    ): Response<Unit>

}