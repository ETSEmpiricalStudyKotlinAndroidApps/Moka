package io.tonnyl.moka.common.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoIntegerType
import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.protobuf.ProtoType

@ExperimentalSerializationApi
@Serializable
actual data class Account(

    @ProtoNumber(1)
    actual val login: String = "",

    @ProtoNumber(2)
    actual val id: Long = 0L,

    @ProtoNumber(3)
    @SerialName("node_id")
    actual val nodeId: String = "",

    @ProtoNumber(4)
    @SerialName("avatar_url")
    actual val avatarUrl: String = "",

    @ProtoNumber(5)
    @SerialName("html_url")
    actual val htmlUrl: String = "",

    @ProtoNumber(6)
    actual val type: String = "",

    @ProtoNumber(7)
    @SerialName("site_admin")
    actual val siteAdmin: Boolean = false,

    @ProtoNumber(8)
    actual val name: String? = null,

    @ProtoNumber(9)
    actual val company: String? = null,

    @ProtoNumber(10)
    actual val blog: String? = null,

    @ProtoNumber(11)
    actual val location: String? = null,

    @ProtoNumber(12)
    actual val email: String? = null,

    @ProtoNumber(13)
    actual val hireable: Boolean? = null,

    @ProtoNumber(14)
    actual val bio: String? = null,

    @ProtoNumber(15)
    @ProtoType(ProtoIntegerType.DEFAULT)
    @SerialName("public_repos")
    actual val publicRepos: Int = 0,

    @ProtoNumber(16)
    @ProtoType(ProtoIntegerType.DEFAULT)
    @SerialName("public_gists")
    actual val publicGists: Int = 0,

    @ProtoNumber(17)
    actual val followers: Long = 0L,

    @ProtoNumber(18)
    actual val following: Long = 0L,

    @ProtoNumber(19)
    @SerialName("created_at")
    @Contextual
    actual val createdAt: String = "",

    @ProtoNumber(20)
    @SerialName("updated_at")
    @Contextual
    actual val updatedAt: String = "",

    @ProtoNumber(21)
    @ProtoType(ProtoIntegerType.DEFAULT)
    @SerialName("private_gists")
    actual val privateGists: Int = 0,

    @ProtoNumber(22)
    @ProtoType(ProtoIntegerType.DEFAULT)
    @SerialName("total_private_repos")
    actual val totalPrivateRepos: Int = 0,

    @ProtoNumber(23)
    @ProtoType(ProtoIntegerType.DEFAULT)
    @SerialName("owned_private_repos")
    actual val ownedPrivateRepos: Int = 0,

    @ProtoNumber(24)
    @SerialName("disk_usage")
    actual val diskUsage: Long = 0L,

    @ProtoNumber(25)
    @ProtoType(ProtoIntegerType.DEFAULT)
    actual val collaborators: Int = 0,

    @ProtoNumber(26)
    @SerialName("two_factor_authentication")
    actual val twoFactorAuthentication: Boolean = false

)