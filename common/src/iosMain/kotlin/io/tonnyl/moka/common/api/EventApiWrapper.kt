package io.tonnyl.moka.common.api

import io.ktor.client.statement.*
import io.tonnyl.moka.common.data.Event
import io.tonnyl.moka.common.network.PageLinks
import io.tonnyl.moka.common.network.api.EventApi
import io.tonnyl.moka.common.serialization.json
import kotlinx.serialization.decodeFromString

@Suppress("unused")
class EventApiWrapper(private val api: EventApi) {

    suspend fun listPublicEventThatAUserHasReceived(
        username: String,
        page: Int,
        perPage: Int
    ): Result<Pair<List<Event>, PageLinks>> {
        return try {
            Result.success(
                value = api.listPublicEventThatAUserHasReceived(
                    username = username,
                    page = page,
                    perPage = perPage
                ).decodeResp()
            )
        } catch (e: Exception) {
            Result.failure(error = e)
        }
    }

    suspend fun listPublicEventThatAUserHasReceivedByUrl(url: String): Result<Pair<List<Event>, PageLinks>> {
        return try {
            Result.success(
                value = api.listPublicEventThatAUserHasReceivedByUrl(url = url).decodeResp()
            )
        } catch (e: Exception) {
            Result.failure(error = e)
        }
    }

    private suspend fun HttpResponse.decodeResp(): Pair<List<Event>, PageLinks> {
        val data = json.decodeFromString<List<Event>>(string = readText())
        val pl = PageLinks(this)

        return Pair(data, pl)
    }

}