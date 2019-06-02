package io.github.tonnyl.moka.network.service

import io.github.tonnyl.moka.data.AuthenticatedUser
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserService {

    @GET("/user")
    fun getAuthenticatedUserAsync(): Deferred<Response<AuthenticatedUser>>

    /**
     * keys: name, email, blog, company, location, bio
     */
    @PATCH("/user")
    fun updateUseInformationAsync(
            @Body params: Map<String, String?>
    ): Deferred<Response<Unit>>

}