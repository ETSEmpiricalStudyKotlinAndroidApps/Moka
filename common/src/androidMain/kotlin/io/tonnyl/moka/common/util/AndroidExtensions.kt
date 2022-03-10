package io.tonnyl.moka.common.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import coil.request.ImageRequest
import io.tonnyl.moka.common.R
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null
                    && activeNetworkInfo.isConnected
        }
    } catch (e: Exception) {
        logcat(priority = LogPriority.ERROR) { e.asLog() }
    }

    return false
}

fun ImageRequest.Builder.createAvatarLoadRequest(): ImageRequest.Builder {
    return apply {
        placeholder(drawableResId = R.drawable.avatar_placeholder)
        error(drawableResId = R.drawable.avatar_placeholder)
        crossfade(enable = true)
    }
}