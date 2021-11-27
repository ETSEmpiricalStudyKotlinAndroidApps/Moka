package io.tonnyl.moka.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventActor(

    var id: Long,

    var login: String,

    @SerialName("avatar_url")
    var avatarUrl: String,

    @SerialName("html_url")
    var htmlUrl: String? = null,

    var type: String? = null

)