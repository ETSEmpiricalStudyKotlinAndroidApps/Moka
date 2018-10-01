package io.github.tonnyl.moka.data

data class AccessToken(
        val accessToken: String,
        val scope: String,
        val tokenType: String
)