package io.tonnyl.moka.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
actual data class Account(

    actual val login: String = "",

    actual val id: Long = 0L,

    @SerialName("node_id")
    actual val nodeId: String = "",

    @SerialName("avatar_url")
    actual val avatarUrl: String = "",

    @SerialName("html_url")
    actual val htmlUrl: String = "",

    actual val type: String = "",

    @SerialName("site_admin")
    actual val siteAdmin: Boolean = false,

    actual val name: String? = null,

    actual val company: String? = null,

    actual val blog: String? = null,

    actual val location: String? = null,

    actual val email: String? = null,

    actual val hireable: Boolean? = null,

    actual val bio: String? = null,

    @SerialName("public_repos")
    actual val publicRepos: Int = 0,

    @SerialName("public_gists")
    actual val publicGists: Int = 0,

    actual val followers: Long = 0L,

    actual val following: Long = 0L,

    @SerialName("created_at")
    actual val createdAt: String = "",

    @SerialName("updated_at")
    actual val updatedAt: String = "",

    @SerialName("private_gists")
    actual val privateGists: Int = 0,

    @SerialName("total_private_repos")
    actual val totalPrivateRepos: Int = 0,

    @SerialName("owned_private_repos")
    actual val ownedPrivateRepos: Int = 0,

    @SerialName("disk_usage")
    actual val diskUsage: Long = 0L,

    actual val collaborators: Int = 0,

    @SerialName("two_factor_authentication")
    actual val twoFactorAuthentication: Boolean = false

)