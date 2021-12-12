package io.tonnyl.moka.common.util

import kotlin.math.ln
import kotlin.math.pow

fun Int.formatWithSuffix(): String {
    if (this < 1000) {
        return toString()
    }
    val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()
    return String.format("%.1f%c", this / 1000.0.pow(exp.toDouble()), "kMGTPE"[exp - 1])
}