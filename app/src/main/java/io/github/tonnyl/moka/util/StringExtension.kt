package io.github.tonnyl.moka.util

fun String?.toShortOid(): String = if (this == null || this.length < 7) "" else this.substring(0, 7)