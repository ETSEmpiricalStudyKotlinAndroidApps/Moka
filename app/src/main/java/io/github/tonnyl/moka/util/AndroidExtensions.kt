package io.github.tonnyl.moka.util

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
import com.google.gson.Gson
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.ui.auth.Authenticator

fun Account.mapToAccountTokenUserTriple(
    gson: Gson,
    manager: AccountManager
): Triple<Account, String, AuthenticatedUser> {
    val token = manager.blockingGetAuthToken(
        this,
        Authenticator.KEY_AUTH_TYPE,
        true
    )
    val user = gson.fromJson(
        manager.getUserData(
            this,
            Authenticator.KEY_AUTH_USER_INFO
        ),
        AuthenticatedUser::class.java
    )
    return Triple(this, token, user)
}

suspend fun AccountManager.insertNewAccount(token: String, user: AuthenticatedUser) {
    val info = Gson().toJson(user)
    val account = Account(user.id.toString(), Authenticator.KEY_ACCOUNT_TYPE)

    addAccountExplicitly(
        account,
        System.currentTimeMillis().toString(),
        Bundle().apply {
            putString(Authenticator.KEY_AUTH_USER_INFO, info)
        })
    setAuthToken(account, Authenticator.KEY_AUTH_TYPE, token)
}

suspend fun AccountManager.moveAccountToFirstPosition(account: Account) {
    val userString = getUserData(account, Authenticator.KEY_AUTH_USER_INFO)
    val token = blockingGetAuthToken(account, Authenticator.KEY_AUTH_TYPE, true)
    removeAccountExplicitly(account)

    insertNewAccount(token, Gson().fromJson(userString, AuthenticatedUser::class.java))
}