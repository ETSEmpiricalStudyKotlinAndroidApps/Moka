package io.tonnyl.moka.common.api

import io.ktor.client.statement.*
import io.tonnyl.moka.common.data.Notification
import io.tonnyl.moka.common.network.PageLinks
import io.tonnyl.moka.common.network.api.NotificationApi
import io.tonnyl.moka.common.serialization.json
import kotlinx.serialization.decodeFromString

@Suppress("unused")
class NotificationApiWrapper(private val api: NotificationApi) {

    suspend fun listNotifications(
        all: Boolean = true,
        page: Int,
        perPage: Int
    ): Result<Pair<List<Notification>, PageLinks>> {
        return try {
            Result.success(
                api.listNotifications(
                    all = all,
                    page = page,
                    perPage = perPage
                ).decodeResp()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listNotificationsByUrl(url: String): Result<Pair<List<Notification>, PageLinks>> {
        return try {
            Result.success(api.listNotificationsByUrl(url = url).decodeResp())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun HttpResponse.decodeResp(): Pair<List<Notification>, PageLinks> {
        val data = json.decodeFromString<List<Notification>>(string = readText())
        val pl = PageLinks(this)

        return Pair(data, pl)
    }

}