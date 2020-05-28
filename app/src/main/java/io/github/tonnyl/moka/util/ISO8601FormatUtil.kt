package io.github.tonnyl.moka.util

import java.text.SimpleDateFormat
import java.util.*

val iso8601Format: SimpleDateFormat by lazy {
    SimpleDateFormat("YYYY-MM-dd'T'HH:MM:SSZ", Locale.US)
}

fun formatISO8601String(date: Date = Calendar.getInstance(Locale.US).time): String {
    return iso8601Format.format(date)
}