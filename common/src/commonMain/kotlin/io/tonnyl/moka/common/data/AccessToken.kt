package io.tonnyl.moka.common.data

expect class AccessToken {

    val accessToken: String

    val scope: String

    val tokenType: String

}