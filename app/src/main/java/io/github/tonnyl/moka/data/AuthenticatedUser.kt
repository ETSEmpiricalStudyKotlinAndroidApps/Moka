package io.github.tonnyl.moka.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.datetime.Instant

@JsonClass(generateAdapter = true)
data class AuthenticatedUser(

    val login: String,

    @Json(name = "id")
    val id: Long,

    @Json(name = "node_id")
    val nodeId: String,

    @Json(name = "avatar_url")
    val avatarUrl: String,

    @Json(name = "html_url")
    val htmlUrl: String,

    val type: String,

    @Json(name = "site_admin")
    val siteAdmin: Boolean,

    val name: String?,

    val company: String?,

    val blog: String?,

    val location: String?,

    val email: String?,

    val hireable: Boolean?,

    val bio: String?,

    @Json(name = "public_repos")
    val publicRepos: Int,

    @Json(name = "public_gists")
    val publicGists: Int,

    val followers: Long,

    val following: Long,

    @Json(name = "created_at")
    val createdAt: Instant,

    @Json(name = "updated_at")
    val updatedAt: Instant,

    @Json(name = "private_gists")
    val privateGists: Int,

    @Json(name = "total_private_repos")
    val totalPrivateRepos: Int,

    @Json(name = "owned_private_repos")
    val ownedPrivateRepos: Int,

    @Json(name = "disk_usage")
    val diskUsage: Long,

    val collaborators: Int,

    @Json(name = "two_factor_authentication")
    val twoFactorAuthentication: Boolean

)