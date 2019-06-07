package io.github.tonnyl.moka.network.service

import io.github.tonnyl.moka.data.AccessToken
import retrofit2.Response
import retrofit2.http.*

interface AccessTokenService {

    /**
     * Exchange code for an access token.
     *
     * @param url Request url.
     * @param clientId Required. The client ID you received from GitHub for your GitHub App.
     * @param clientSecret Required. The client secret you received from GitHub for your GitHub App.
     * @param code Required. The code you received.
     * @param redirectUri The URL in your application where users are sent after authorization.
     * @param state The unguessable random string.
     */
    @Headers(value = ["Accept: application/json"])
    @POST
    @FormUrlEncoded
    suspend fun getAccessToken(
            @Url url: String,
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("code") code: String,
            @Field("redirect_uri") redirectUri: String,
            @Field("state") state: String
    ): Response<AccessToken>

}