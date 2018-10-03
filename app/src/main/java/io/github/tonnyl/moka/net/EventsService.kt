package io.github.tonnyl.moka.net

import io.github.tonnyl.moka.data.Event
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EventsService {

    /**
     * These are events that you've received by watching repos and following users.
     * If you are authenticated as the given user, you will see private events.
     * Otherwise, you'll only see public events.
     */
    @GET("users/{username}/received_events")
    fun listEventThatAUserHasReceived(@Path("username") username: String): Observable<Response<List<Event>>>

}