package io.github.tonnyl.moka.network.service

import io.github.tonnyl.moka.data.AuthenticatedUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserService {

    @GET("/user")
    suspend fun getAuthenticatedUser(): Response<AuthenticatedUser>

    /**
     * keys: name, email, blog, company, location, bio
     */
    @PATCH("/user")
    suspend fun updateUseInformation(
            @Body params: Map<String, String?>
    ): Response<Unit>

}