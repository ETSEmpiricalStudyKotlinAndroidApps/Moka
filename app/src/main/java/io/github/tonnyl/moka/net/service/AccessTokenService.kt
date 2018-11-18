package io.github.tonnyl.moka.net.service

import io.github.tonnyl.moka.data.AccessToken
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

interface AccessTokenService {

    @POST
    @FormUrlEncoded
    fun getAccessToken(
            @Url url: String,
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("code") code: String,
            @Field("redirect_uri") redirectUri: String
    ): Single<AccessToken>

}