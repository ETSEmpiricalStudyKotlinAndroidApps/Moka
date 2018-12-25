package io.github.tonnyl.moka.util

import java.text.SimpleDateFormat
import java.util.*


private val iso8601Format: SimpleDateFormat by lazy {
    SimpleDateFormat("YYYY-MM-DD'T'HH:MM:SSZ", Locale.US)
}

fun formatISO8601String(date: Date = Calendar.getInstance(Locale.US).time): String = iso8601Format.format(date)

