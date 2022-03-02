package io.tonnyl.moka.common.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@ExperimentalSerializationApi
@Serializable
actual data class SignedInAccount(

    @ProtoNumber(1)
    @SerialName("access_token")
    actual val accessToken: AccessToken = AccessToken(),

    @ProtoNumber(2)
    actual val account: Account = Account()

)