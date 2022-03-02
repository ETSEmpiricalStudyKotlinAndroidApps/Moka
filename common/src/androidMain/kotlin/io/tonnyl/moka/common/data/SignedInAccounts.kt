package io.tonnyl.moka.common.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@ExperimentalSerializationApi
@Serializable
actual data class SignedInAccounts(

    @ProtoNumber(1)
    actual val accounts: List<SignedInAccount> = emptyList()

)