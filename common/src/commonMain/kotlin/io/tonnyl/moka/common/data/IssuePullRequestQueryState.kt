package io.tonnyl.moka.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class IssuePullRequestQueryState(val rawValue: String) {

    @SerialName("open")
    Open("open"),

    @SerialName("closed")
    Closed("closed"),

    @SerialName("all")
    All("all"),

}