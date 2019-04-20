package io.github.tonnyl.moka.network.service

import io.github.tonnyl.moka.data.AuthenticatedUser
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserService {

    @GET("/user")
    fun getAuthenticatedUser(): Single<Response<AuthenticatedUser>>

    /**
     * keys: name, email, blog, company, location, bio
     */
    @PATCH("/user")
    fun updateUseInformation(
            @Body params: Map<String, String?>
    ): Single<Response<Unit>>

}