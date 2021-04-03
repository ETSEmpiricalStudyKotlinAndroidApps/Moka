package io.github.tonnyl.moka.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticatedUser(

    val login: String,

    @SerialName("id")
    val id: Long,

    @SerialName("node_id")
    val nodeId: String,

    @SerialName("avatar_url")
    val avatarUrl: String,

    @SerialName("html_url")
    val htmlUrl: String,

    val type: String,

    @SerialName("site_admin")
    val siteAdmin: Boolean,

    val name: String?,

    val company: String?,

    val blog: String?,

    val location: String?,

    val email: String?,

    val hireable: Boolean?,

    val bio: String?,

    @SerialName("public_repos")
    val publicRepos: Int,

    @SerialName("public_gists")
    val publicGists: Int,

    val followers: Long,

    val following: Long,

    @SerialName("created_at")
    @Contextual
    val createdAt: Instant,

    @SerialName("updated_at")
    @Contextual
    val updatedAt: Instant,

    @SerialName("private_gists")
    val privateGists: Int,

    @SerialName("total_private_repos")
    val totalPrivateRepos: Int,

    @SerialName("owned_private_repos")
    val ownedPrivateRepos: Int,

    @SerialName("disk_usage")
    val diskUsage: Long,

    val collaborators: Int,

    @SerialName("two_factor_authentication")
    val twoFactorAuthentication: Boolean

)