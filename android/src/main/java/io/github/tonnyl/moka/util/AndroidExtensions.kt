package io.github.tonnyl.moka.util

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Environment
import android.os.Looper
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo3.exception.ApolloNetworkException
import io.github.tonnyl.moka.R
import io.tonnyl.moka.common.data.Account
import io.tonnyl.moka.common.data.Emoji
import io.tonnyl.moka.common.serialization.json
import io.tonnyl.moka.graphql.ViewerQuery
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import okio.buffer
import okio.source
import java.io.File
import java.nio.channels.UnresolvedAddressException
import java.text.DateFormat
import java.util.*

private const val TAG = "AndroidExtensions"

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

fun Instant.formatDateWithDefaultLocale(
    dateStyle: Int = DateFormat.DEFAULT,
    timeStyle: Int = DateFormat.SHORT
): String {
    val date = Date(toEpochMilliseconds())
    return DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.getDefault())
        .format(date)
}

val Throwable?.displayExceptionDetails: Boolean
    get() = this != null
            && this !is ApolloNetworkException
            && this !is UnresolvedAddressException

fun Context.shareText(text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    safeStartActivity(shareIntent)
}

fun Context.openInBrowser(url: String) {
    CustomTabsIntent.Builder()
        .build()
        .launchUrl(this, Uri.parse(url))
}

fun ViewerQuery.Viewer.toAccount(existing: Account): Account {
    val viewer = this.user
    return Account(
        login = viewer.login,
        id = existing.id,
        nodeId = viewer.id,
        avatarUrl = viewer.avatarUrl,
        htmlUrl = viewer.url,
        type = existing.type,
        siteAdmin = viewer.isSiteAdmin,
        name = viewer.name,
        company = viewer.company,
        blog = viewer.websiteUrl,
        location = viewer.location,
        email = viewer.email,
        hireable = viewer.isHireable,
        bio = viewer.bio,
        publicRepos = existing.publicRepos,
        publicGists = existing.publicGists,
        followers = viewer.followers.totalCount.toLong(),
        following = viewer.following.totalCount.toLong(),
        createdAt = viewer.createdAt.toString(),
        updatedAt = viewer.updatedAt.toString(),
        privateGists = existing.privateGists,
        totalPrivateRepos = existing.totalPrivateRepos,
        ownedPrivateRepos = existing.ownedPrivateRepos,
        diskUsage = existing.diskUsage,
        collaborators = existing.collaborators,
        twoFactorAuthentication = existing.twoFactorAuthentication
    )
}