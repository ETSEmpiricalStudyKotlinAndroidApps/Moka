package io.github.tonnyl.moka.net.service

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH

interface UserService {

    /**
     * keys: name, email, blog, company, location, bio
     */
    @PATCH("/user")
    fun updateUseInformation(
            @Body params: Map<String, String?>
    ): Single<Response<Unit>>

}