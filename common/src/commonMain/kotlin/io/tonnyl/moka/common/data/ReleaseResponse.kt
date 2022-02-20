package io.tonnyl.moka.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReleaseResponse(

    @SerialName("tag_name")
    val tagName: String

)