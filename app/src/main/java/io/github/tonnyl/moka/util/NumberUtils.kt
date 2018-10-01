package io.github.tonnyl.moka.util

fun formatNumberWithSuffix(number: Int): String {
    if (number < 1000) return number.toString()
    val exp = (Math.log(number.toDouble()) / Math.log(1000.0)).toInt()
    return String.format("%.1f%c", number / Math.pow(1000.0, exp.toDouble()), "kMGTPE"[exp - 1])
}