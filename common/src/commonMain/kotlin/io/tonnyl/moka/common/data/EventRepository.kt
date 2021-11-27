package io.tonnyl.moka.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventRepository(

    var id: String,

    var name: String,

    @SerialName("full_name")
    var fullName: String? = null,

    var url: String,

    @SerialName("html_url")
    var htmlUrl: String? = null

)