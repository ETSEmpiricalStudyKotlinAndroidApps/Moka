package io.github.tonnyl.moka.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class AuthenticatedUser(

    @SerializedName("login")
    val login: String,

    @SerializedName("id")
    val id: Long,

    @SerializedName("node_id")
    val nodeId: String,

    @SerializedName("avatar_url")
    val avatarUrl: String,

    @SerializedName("html_url")
    val htmlUrl: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("site_admin")
    val siteAdmin: Boolean,

    @SerializedName("name")
    val name: String?,

    @SerializedName("company")
    val company: String?,

    @SerializedName("blog")
    val blog: String?,

    @SerializedName("location")
    val location: String?,

    @SerializedName("email")
    val email: String,

    @SerializedName("hireable")
    val hireable: Boolean,

    @SerializedName("bio")
    val bio: String?,

    @SerializedName("public_repos")
    val publicRepos: Int,

    @SerializedName("public_gists")
    val publicGists: Int,

    @SerializedName("followers")
    val followers: Long,

    @SerializedName("following")
    val following: Long,

    @SerializedName("created_at")
    val createdAt: Date,

    @SerializedName("updated_at")
    val updatedAt: Date,

    @SerializedName("private_gists")
    val privateGists: Int,

    @SerializedName("total_private_repos")
    val totalPrivateRepos: Int,

    @SerializedName("owned_private_repos")
    val ownedPrivateRepos: Int,

    @SerializedName("disk_usage")
    val diskUsage: Long,

    @SerializedName("collaborators")
    val collaborators: Int,

    @SerializedName("two_factor_authentication")
    val twoFactorAuthentication: Boolean

) : Parcelable