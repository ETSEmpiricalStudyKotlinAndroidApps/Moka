package io.tonnyl.moka.common.extensions

val String?.shortOid: String
    get() = if (this == null || this.length < 7) {
        ""
    } else {
        substring(0, 7)
    }

val String?.orGhostAvatarUrl: String
    get() = if (this.isNullOrEmpty()) {
        "https://avatars.githubusercontent.com/u/10137"
    } else {
        this
    }