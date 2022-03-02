package io.tonnyl.moka.common.data

expect class Account {

    val login: String

    val id: Long

    val nodeId: String

    val avatarUrl: String

    val htmlUrl: String

    val type: String

    val siteAdmin: Boolean

    val name: String?

    val company: String?

    val blog: String?

    val location: String?

    val email: String?

    val hireable: Boolean?

    val bio: String?

    val publicRepos: Int

    val publicGists: Int

    val followers: Long

    val following: Long

    val createdAt: String

    val updatedAt: String

    val privateGists: Int

    val totalPrivateRepos: Int

    val ownedPrivateRepos: Int

    val diskUsage: Long

    val collaborators: Int

    val twoFactorAuthentication: Boolean

}