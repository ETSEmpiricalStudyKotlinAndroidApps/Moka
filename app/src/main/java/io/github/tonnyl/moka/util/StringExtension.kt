package io.github.tonnyl.moka.util

import android.graphics.Color
import android.text.Spanned
import androidx.annotation.ColorInt
import androidx.core.text.HtmlCompat
import timber.log.Timber

fun String?.toShortOid(): String = if (this == null || this.length < 7) "" else this.substring(0, 7)

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
        Timber.e(e, "string convert to color failed")
    }

    return null
}

fun String.toHtmlInLegacyMode(): Spanned {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
}