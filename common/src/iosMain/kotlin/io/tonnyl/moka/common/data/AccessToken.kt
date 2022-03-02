package io.tonnyl.moka.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
actual data class AccessToken(

    @SerialName("access_token")
    actual val accessToken: String = "",

    actual val scope: String = "",

    @SerialName("token_type")
    actual val tokenType: String = ""

)