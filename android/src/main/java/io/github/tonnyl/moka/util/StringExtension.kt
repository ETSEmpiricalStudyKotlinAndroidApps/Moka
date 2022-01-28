package io.github.tonnyl.moka.util

import android.graphics.Color
import androidx.annotation.ColorInt
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Convert a string to a color int value. If failed, return null.
 */
@ColorInt
fun String.toColor(): Int? {
    try {
        if (isEmpty()) {
            return null
        }

        var color = this
        if (!color.startsWith("#")) {
            color = "#$color"
        }

        // convert #RGB to #RRGGBB
        if (color.length == 4) {
            color = "${color[1]}${color[1]}${color[2]}${color[2]}${color[3]}${color[3]}"
        }

        return Color.parseColor(color)
    } catch (e: Exception) {
        logcat(priority = LogPriority.ERROR) { "string convert to color failed\n${e.asLog()}" }
    }

    return null
}

fun String.mkdirs(): String {
    File(this).apply {
        if (!exists()) {
            mkdirs()
        }
    }
    return this
}

val String.md5: String
   get() {
       val digest = MessageDigest.getInstance("MD5")
       return BigInteger(1, digest.digest(this.toByteArray())).toString(16).padStart(32, '0')
   }