package io.tonnyl.moka.common.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
actual data class SignedInAccounts(

    @ProtoNumber(1)
    actual val accounts: List<SignedInAccount> = emptyList()

)