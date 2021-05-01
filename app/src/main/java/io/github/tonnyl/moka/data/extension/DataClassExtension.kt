package io.github.tonnyl.moka.data.extension

import io.github.tonnyl.moka.data.AccessToken
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.fragment.PageInfo
import io.github.tonnyl.moka.serializers.store.data.Account
import kotlinx.serialization.ExperimentalSerializationApi
import io.github.tonnyl.moka.serializers.store.data.AccessToken as PBAccessToken

val PageInfo?.checkedStartCursor: String?
    get() {
        return if (this?.hasPreviousPage == true) {
            startCursor
        } else {
            null
        }
    }

val PageInfo?.checkedEndCursor: String?
    get() {
        return if (this?.hasNextPage == true) {
            endCursor
        } else {
            null
        }
    }

@ExperimentalSerializationApi
fun AuthenticatedUser.toPbAccount(): Account {
    val user = this
    return Account(
        login = user.login,
        id = user.id,
        nodeId = user.nodeId,
        avatarUrl = user.avatarUrl,
        htmlUrl = user.htmlUrl,
        type = user.type,
        siteAdmin = user.siteAdmin,
        name = user.name,
        company = user.company,
        blog = user.blog,
        location = user.location,
        email = user.email,
        hireable = user.hireable,
        bio = user.bio,
        publicRepos = user.publicRepos,
        publicGists = user.publicGists,
        followers = user.followers,
        following = user.following,
        createdAt = user.createdAt.toString(),
        updatedAt = user.updatedAt.toString(),
        privateGists = user.privateGists,
        totalPrivateRepos = user.totalPrivateRepos,
        ownedPrivateRepos = user.ownedPrivateRepos,
        diskUsage = user.diskUsage,
        collaborators = user.collaborators,
        twoFactorAuthentication = user.twoFactorAuthentication
    )
}

@ExperimentalSerializationApi
fun AccessToken.toPBAccessToken(): PBAccessToken {
    val token = this
    return PBAccessToken(
        accessToken = token.accessToken,
        scope = token.scope,
        tokenType = token.tokenType
    )
}