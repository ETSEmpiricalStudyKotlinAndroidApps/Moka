package io.github.tonnyl.moka.util

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.data.Emoji
import io.github.tonnyl.moka.ui.auth.Authenticator
import timber.log.Timber
import java.nio.charset.Charset

fun Account.mapToAccountTokenUserTriple(
    manager: AccountManager
): Triple<Account, String, AuthenticatedUser>? {
    val token = manager.blockingGetAuthToken(
        this,
        Authenticator.KEY_AUTH_TYPE,
        true
    )
    val user = MoshiInstance.authenticatedUserAdapter
        .fromJson(
            manager.getUserData(
                this,
                Authenticator.KEY_AUTH_USER_INFO
            )
        ) ?: return null
    return Triple(this, token, user)
}

suspend fun AccountManager.insertNewAccount(token: String, user: AuthenticatedUser) {
    val info = MoshiInstance.authenticatedUserAdapter
        .toJson(user)
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

    MoshiInstance.authenticatedUserAdapter.fromJson(userString)?.let {
        insertNewAccount(token, it)
    }
}

fun AppCompatActivity.updateForTheme() {
    (applicationContext as MokaApp).theme.observe(this, Observer { value ->
        delegate.localNightMode = when (value) {
            "0" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                } else {
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                }
            }
            "1" -> {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            "2" -> {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            else -> {
                throw IllegalArgumentException("invalid theme value: $value")
            }
        }
    })
}

val Resources.isDarkModeOn: Boolean
    get() = (configuration.uiMode and Configuration.UI_MODE_NIGHT_YES) == Configuration.UI_MODE_NIGHT_YES

fun Context.readEmojisFromAssets(): List<Emoji> {
    return assets.open("emojis.json").use { inputStream ->
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        val json = String(buffer, Charset.forName("UTF-8"))
        MoshiInstance.emojiListAdapter.fromJson(json) ?: emptyList()
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