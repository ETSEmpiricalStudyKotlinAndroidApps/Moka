package io.tonnyl.moka.common.data

import kotlinx.serialization.Serializable

@Serializable
actual data class SignedInAccounts(

    actual val accounts: List<SignedInAccount> = emptyList()

)