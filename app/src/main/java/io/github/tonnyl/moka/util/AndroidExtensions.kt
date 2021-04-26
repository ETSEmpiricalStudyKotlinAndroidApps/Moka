package io.github.tonnyl.moka.util

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import io.github.tonnyl.moka.data.AccessToken
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.data.Emoji
import io.github.tonnyl.moka.ui.auth.Authenticator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import okio.buffer
import okio.source
import timber.log.Timber

fun Account.mapToAccountTokenUserTriple(
    manager: AccountManager
): Triple<Account, String, AuthenticatedUser>? {
    val token = manager.blockingGetAuthToken(
        this,
        Authenticator.KEY_AUTH_TOKEN,
        true
    )
    val user = runCatching {
        json.decodeFromString<AuthenticatedUser>(
            manager.getUserData(
                this,
                Authenticator.KEY_AUTH_USER_INFO
            )
        )
    }.getOrNull() ?: return null
    return Triple(this, token, user)
}

fun AccountManager.insertNewAccount(token: AccessToken, user: AuthenticatedUser) {
    val userString = runCatching {
        json.encodeToString(user)
    }.getOrNull() ?: return
    val tokenString = kotlin.runCatching {
        json.encodeToString(token)
    }.getOrNull() ?: return

    val account = Account(user.id.toString(), Authenticator.KEY_ACCOUNT_TYPE)

    addAccountExplicitly(
        account,
        System.currentTimeMillis().toString(),
        Bundle().apply {
            putString(Authenticator.KEY_AUTH_USER_INFO, userString)
        })
    setAuthToken(account, Authenticator.KEY_AUTH_TOKEN, tokenString)
}

val Resources.isDarkModeOn: Boolean
    get() = (configuration.uiMode and Configuration.UI_MODE_NIGHT_YES) == Configuration.UI_MODE_NIGHT_YES

fun Context.readEmojisFromAssets(): List<Emoji> {
    return assets.open("emojis.json").use { inputStream ->
        val jsonString = inputStream.source().buffer().readString(Charsets.UTF_8)
        json.decodeFromString(jsonString)
    }
}

/**
 * Update this live data on any thread. This extension function will do the boring stuff internally for you.
 */
fun <T> MutableLiveData<T>.updateOnAnyThread(newValue: T) {
    if (Looper.getMainLooper().isCurrentThread) { // on main thread
        value = newValue
    } else {
        postValue(newValue)
    }
}

fun Context.safeStartActivity(
    intent: Intent,
    actionWhenSuccess: (() -> Unit)? = null,
    actionWhenError: ((Throwable) -> Unit)? = null
) {
    try {
        startActivity(intent)

        actionWhenSuccess?.invoke()
    } catch (e: Exception) {
        Timber.e(e)

        actionWhenError?.invoke(e)
    }
}