package io.github.tonnyl.moka.data

import io.tonnyl.moka.graphql.fragment.Topic as RawTopic

data class Topic(

    val id: String,

    /**
     * The topic's name.
     */
    val name: String,

    /**
     * Returns a boolean indicating whether the viewing user has starred this starrable.
     */
    val viewerHasStarred: Boolean

)

fun RawTopic.toNonNullTopic(): Topic {
    return Topic(id, name, viewerHasStarred)
}