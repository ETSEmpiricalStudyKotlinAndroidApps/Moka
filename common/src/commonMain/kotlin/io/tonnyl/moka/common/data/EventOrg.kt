package io.tonnyl.moka.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventOrg(

    var id: Int,

    var login: String,

    @SerialName("gravatar_id")
    var grAvatarId: String,

    var url: String,

    @SerialName("avatar_url")
    var avatarUrl: String

)