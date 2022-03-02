package io.tonnyl.moka.common.data

expect class SignedInAccount {

    val accessToken: AccessToken

    val account: Account

}