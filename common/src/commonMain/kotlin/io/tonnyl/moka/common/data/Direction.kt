package io.tonnyl.moka.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Direction(val rawValue: String) {

    @SerialName("asc")
    Ascending("asc"),

    @SerialName("desc")
    Descending("desc")

}