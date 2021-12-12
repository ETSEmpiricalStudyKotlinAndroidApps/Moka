package io.tonnyl.moka.common.util

fun String?.toShortOid(): String = if (this == null || this.length < 7) "" else this.substring(0, 7)