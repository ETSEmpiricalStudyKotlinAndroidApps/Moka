package io.github.tonnyl.moka.data.extension

import com.google.protobuf.BoolValue
import com.google.protobuf.StringValue
import io.github.tonnyl.moka.data.AccessToken
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.fragment.PageInfo
import io.github.tonnyl.moka.proto.Account
import io.github.tonnyl.moka.proto.AccessToken as PBAccessToken

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

fun AuthenticatedUser.toPbAccount(): Account {
    val user = this
    return Account.newBuilder().apply {
        login = user.login
        id = user.id
        nodeId = user.nodeId
        avatarUrl = user.avatarUrl
        htmlUrl = user.htmlUrl
        type = user.type
        siteAdmin = user.siteAdmin
        if (!user.name.isNullOrEmpty()) {
            name = StringValue.newBuilder().apply {
                value = user.name
            }.build()
        }
        if (!user.company.isNullOrEmpty()) {
            company = StringValue.newBuilder().apply {
                value = user.company
            }.build()
        }
        if (!user.blog.isNullOrEmpty()) {
            blog = StringValue.newBuilder().apply {
                value = user.blog
            }.build()
        }
        if (!user.location.isNullOrEmpty()) {
            location = StringValue.newBuilder().apply {
                value = user.location
            }.build()
        }
        if (!user.email.isNullOrEmpty()) {
            email = StringValue.newBuilder().apply {
                value = user.email
            }.build()
        }
        if (user.hireable != null) {
            hireable = BoolValue.newBuilder().apply {
                value = user.hireable
            }.build()
        }
        if (!user.bio.isNullOrEmpty()) {
            bio = StringValue.newBuilder().apply {
                value = user.bio
            }.build()
        }
        publicRepos = user.publicRepos
        publicGists = user.publicGists
        followers = user.followers
        following = user.following
        createdAt = user.createdAt.toString()
        updatedAt = user.updatedAt.toString()
        privateGists = user.privateGists
        totalPrivateRepos = user.totalPrivateRepos
        ownedPrivateRepos = user.ownedPrivateRepos
        diskUsage = user.diskUsage
        collaborators = user.collaborators
        twoFactorAuthentication = user.twoFactorAuthentication
    }.build()
}

fun AccessToken.toPBAccessToken(): PBAccessToken {
    val token = this
    return PBAccessToken.newBuilder().apply {
        accessToken = token.accessToken
        scope = token.scope
        tokenType = token.tokenType
    }.build()
}