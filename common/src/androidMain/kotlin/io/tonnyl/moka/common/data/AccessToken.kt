package io.tonnyl.moka.common.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@ExperimentalSerializationApi
@Serializable
actual data class AccessToken(

    @ProtoNumber(1)
    @SerialName("access_token")
    actual val accessToken: String = "",

    @ProtoNumber(2)
    actual val scope: String = "",

    @ProtoNumber(3)
    @SerialName("token_type")
    actual val tokenType: String = ""

)