package io.tonnyl.moka.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
actual data class SignedInAccount(

    @SerialName("access_token")
    actual val accessToken: AccessToken = AccessToken(),

    actual val account: Account = Account()

)