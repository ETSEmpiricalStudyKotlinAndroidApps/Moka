package io.tonnyl.moka.common.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Gist(

    var id: String,

    @SerialName("html_url")
    var htmlUrl: String,

    @Contextual
    var files: Map<String, EventGistFile>,

    // note the difference of serialized name, column name and field name
    @SerialName("public")
    var isPublic: Boolean,

    @SerialName("created_at")
    @Contextual
    var createdAt: Instant,

    @SerialName("updated_at")
    @Contextual
    var updatedAt: Instant,

    var description: String,

    var comments: Int,

    @Contextual
    var owner: EventActor,

    var truncated: Boolean

)