package io.github.tonnyl.moka.util

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.auth.Authenticator
import io.tonnyl.moka.common.data.AccessToken
import io.tonnyl.moka.common.data.AuthenticatedUser
import io.tonnyl.moka.common.data.Emoji
import io.tonnyl.moka.common.serialization.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import okio.buffer
import okio.source
import java.io.File

private const val TAG = "AndroidExtensions"

@ExperimentalPagingApi
@ExperimentalSerializationApi
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

@ExperimentalSerializationApi
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
        logcat(priority = LogPriority.ERROR) { e.asLog() }

        actionWhenError?.invoke(e)
    }
}

fun Context.shareMedia(
    uri: Uri,
    mimeType: String?
) {
    safeStartActivity(
        Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = mimeType
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            },
            null
        ).apply {
            if (this@shareMedia !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    )
}

fun Context.downloadFileViaDownloadManager(
    url: String,
    accessToken: String?
) {
    try {
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            if (accessToken.isNullOrEmpty()) {
                addRequestHeader("Authorization", "Bearer $accessToken")
            }
            val filename = File(url).name
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
            setTitle(filename)
            setDescription(this@downloadFileViaDownloadManager.getString(R.string.media_downloading))
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }

        (getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager)?.enqueue(request)
    } catch (e: Exception) {
        logcat(tag = TAG, priority = LogPriority.ERROR) {
            "failed to download file via download manager: ${e.asLog()}"
        }
    }
}