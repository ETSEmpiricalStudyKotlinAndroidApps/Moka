package io.tonnyl.moka.common.store.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoIntegerType
import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.protobuf.ProtoType

@ExperimentalSerializationApi
@Serializable
data class Account(

    @ProtoNumber(1)
    val login: String = "",

    @ProtoNumber(2)
    val id: Long = 0L,

    @ProtoNumber(3)
    val nodeId: String = "",

    @ProtoNumber(4)
    val avatarUrl: String = "",

    @ProtoNumber(5)
    val htmlUrl: String = "",

    @ProtoNumber(6)
    val type: String = "",

    @ProtoNumber(7)
    val siteAdmin: Boolean = false,

    @ProtoNumber(8)
    val name: String? = null,

    @ProtoNumber(9)
    val company: String? = null,

    @ProtoNumber(10)
    val blog: String? = null,

    @ProtoNumber(11)
    val location: String? = null,

    @ProtoNumber(12)
    val email: String? = null,

    @ProtoNumber(13)
    val hireable: Boolean? = null,

    @ProtoNumber(14)
    val bio: String? = null,

    @ProtoNumber(15)
    @ProtoType(ProtoIntegerType.DEFAULT)
    val publicRepos: Int = 0,

    @ProtoNumber(16)
    @ProtoType(ProtoIntegerType.DEFAULT)
    val publicGists: Int = 0,

    @ProtoNumber(17)
    val followers: Long = 0L,

    @ProtoNumber(18)
    val following: Long = 0L,

    @ProtoNumber(19)
    val createdAt: String = "",

    @ProtoNumber(20)
    val updatedAt: String = "",

    @ProtoNumber(21)
    @ProtoType(ProtoIntegerType.DEFAULT)
    val privateGists: Int = 0,

    @ProtoNumber(22)
    @ProtoType(ProtoIntegerType.DEFAULT)
    val totalPrivateRepos: Int = 0,

    @ProtoNumber(23)
    @ProtoType(ProtoIntegerType.DEFAULT)
    val ownedPrivateRepos: Int = 0,

    @ProtoNumber(24)
    val diskUsage: Long = 0L,

    @ProtoNumber(25)
    @ProtoType(ProtoIntegerType.DEFAULT)
    val collaborators: Int = 0,

    @ProtoNumber(26)
    val twoFactorAuthentication: Boolean = false

)

@ExperimentalSerializationApi
@Serializable
data class AccessToken(

    @ProtoNumber(1)
    val accessToken: String = "",

    @ProtoNumber(2)
    val scope: String = "",

    @ProtoNumber(3)
    val tokenType: String = ""

)

@ExperimentalSerializationApi
@Serializable
data class SignedInAccount(

    @ProtoNumber(1)
    val accessToken: AccessToken = AccessToken(),

    @ProtoNumber(2)
    val account: Account = Account()

)

@ExperimentalSerializationApi
@Serializable
data class SignedInAccounts(

    @ProtoNumber(1)
    val accounts: List<SignedInAccount> = emptyList()

)