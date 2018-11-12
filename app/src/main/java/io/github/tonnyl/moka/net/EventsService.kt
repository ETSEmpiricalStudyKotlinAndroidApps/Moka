package io.github.tonnyl.moka.net

import io.github.tonnyl.moka.data.Event
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface EventsService {

    /**
     * These are events that you've received by watching repos and following users.
     * If you are authenticated as the given user, you will see private events.
     * Otherwise, you'll only see public events.
     */
    @GET("users/{username}/received_events/public")
    fun listPublicEventThatAUserHasReceived(
            @Path("username") username: String,
            @Query("page") page: Int,
            @Query("per_page") perPage: Int
    ): Call<List<Event>>

    @GET
    fun listPublicEventThatAUserHasReceivedByUrl(
            @Url url: String
    ): Call<List<Event>>

}