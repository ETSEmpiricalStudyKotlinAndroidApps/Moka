package io.github.tonnyl.moka.data.extension

import io.github.tonnyl.moka.fragment.PageInfo

val PageInfo?.checkedStartCursor: String?
    get() {
        return if (this?.hasPreviousPage == true) {
            startCursor
        } else {
            null
        }
    }

val PageInfo?.checkedEndCursor: String?
    get() {
        return if (this?.hasNextPage == true) {
            endCursor
        } else {
            null
        }
    }